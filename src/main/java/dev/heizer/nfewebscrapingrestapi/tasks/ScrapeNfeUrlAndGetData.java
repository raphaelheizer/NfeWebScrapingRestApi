package dev.heizer.nfewebscrapingrestapi.tasks;

import dev.heizer.nfewebscrapingrestapi.exceptions.NonUniqueResultException;
import dev.heizer.nfewebscrapingrestapi.interfaces.TextMatchingValidator;
import dev.heizer.nfewebscrapingrestapi.models.Authorizer;
import dev.heizer.nfewebscrapingrestapi.models.Service;
import dev.heizer.nfewebscrapingrestapi.models.ServiceStatus;
import dev.heizer.nfewebscrapingrestapi.util.ServiceStatusValidator;
import dev.heizer.nfewebscrapingrestapi.util.Validator;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO: Document me
 */
public class ScrapeNfeUrlAndGetData implements Runnable
{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final List<Authorizer> authorizers = new ArrayList<>();

    @Override
    public void run()
    {
        Connection connection = Jsoup.connect("https://www.nfe.fazenda.gov.br/portal/disponibilidade.aspx");
        Document document;

        try {document = connection.get();}
        catch (IOException ioe)
        {
            logger.error("Could not get URL content. Aborting current task execution", ioe);
            return;
        }

        final Element serviceTableElement = getUniqueElementFromDocument(document, ".tabelaListagemDados");
        final Elements tableRows = serviceTableElement.getElementsByTag("tr");

        final List<String> services = tableRows.select("th")
                                               .stream()
                                               .map(Element::text)
                                               .collect(Collectors.toList());

        final List<Element> tableRowElements = tableRows.stream().skip(1).collect(Collectors.toList());

        tableRowElements.forEach(tableElement -> { // Serviços + status
            Authorizer authorizer = new Authorizer();
            Element systemName = tableElement.children().select("td").first(); // System!

            if (systemName != null)
                authorizer.setName(systemName.text());
            else
            {
                logger.error("Unable to set system name. Aborting.");
                return;
            }

            List<ServiceStatus> statusTexts = tableElement.children()
                                                          .stream()
                                                          .skip(1) // Should not be used with paralelism
                                                          .map(Element::children)
                                                          .map(newElement -> newElement.attr("src"))
                                                          .map(this::parseStatus)
                                                          .collect(Collectors.toList());

            for (int i = 0; i < statusTexts.size(); i++)
            {
                Service service = new Service();
                service.setName(services.get(i));
                service.setStatus(statusTexts.get(i));

                services.forEach(item -> {authorizer.getServices().add(service);});
            }
            authorizers.add(authorizer);
        });

        authorizers.forEach(System.out::println); // TODO: Save in database
    }

    private Element getUniqueElementFromDocument(Document document, String selectQuery)
    {
        Elements servicesTableElements = document.select(selectQuery);

        if (servicesTableElements.size() > 1)
            throw new NonUniqueResultException();

        return servicesTableElements.first();
    }

    private ServiceStatus parseStatus(String text)
    {
        List<TextMatchingValidator<ServiceStatus>> validatorList = new ArrayList<>();

        Validator<ServiceStatus> validator = new Validator<ServiceStatus>()
                .setDefaultCase(ServiceStatus.SERVICE_NOT_REGISTERED);

        validatorList.add(new ServiceStatusValidator<>("verde", ServiceStatus.SERVICE_AVAILABLE));
        validatorList.add(new ServiceStatusValidator<>("vermelho", ServiceStatus.SERVICE_UNAVAILABLE));
        validatorList.add(new ServiceStatusValidator<>("amarelo", ServiceStatus.SERVICE_UNSTABLE));

        return validator.validate(validatorList, text);
    }
}