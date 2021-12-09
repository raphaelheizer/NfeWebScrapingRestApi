package dev.heizer.nfewebscrapingrestapi.tasks;

import dev.heizer.nfewebscrapingrestapi.exceptions.NonUniqueResultException;
import dev.heizer.nfewebscrapingrestapi.interfaces.TextMatchingValidator;
import dev.heizer.nfewebscrapingrestapi.models.Authorizer;
import dev.heizer.nfewebscrapingrestapi.models.NfeService;
import dev.heizer.nfewebscrapingrestapi.models.NfeServiceHistory;
import dev.heizer.nfewebscrapingrestapi.models.NfeServiceStatusEnum;
import dev.heizer.nfewebscrapingrestapi.repositories.AuthorizerRepository;
import dev.heizer.nfewebscrapingrestapi.repositories.NfeServiceHistoryRepository;
import dev.heizer.nfewebscrapingrestapi.repositories.NfeServiceRepository;
import dev.heizer.nfewebscrapingrestapi.util.ServiceStatusValidator;
import dev.heizer.nfewebscrapingrestapi.util.Validator;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static dev.heizer.nfewebscrapingrestapi.models.NfeServiceStatusEnum.*;

/**
 * Scrapes {@value #URL_TO_SCRAPE} and persists data into the database at a fixed interval
 */
@Service
public class ScrapeNfeUrlAndGetData implements Runnable
{
    private static final String URL_TO_SCRAPE = "https://www.nfe.fazenda.gov.br/portal/disponibilidade.aspx";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final List<NfeServiceHistory> serviceHistories = new ArrayList<>();
    private NfeServiceHistoryRepository nfeServiceHistoryRepository;
    private NfeServiceRepository nfeServiceRepository;
    private AuthorizerRepository authorizerRepository;

    @Override
    public void run()
    {
        Connection connection = Jsoup.connect(URL_TO_SCRAPE);
        Document document;

        try {document = connection.get();}
        catch (IOException ioe)
        {
            logger.error("Could not get URL content. Aborting current task execution", ioe);
            return;
        }

        final Element serviceTableElement = getTabelaListagemDadosFromDocument(document);
        final Elements tableRows = serviceTableElement.getElementsByTag("tr");

        final List<String> services = tableRows.select("th")
                                               .stream()
                                               .skip(1)
                                               .map(Element::text)
                                               .collect(Collectors.toList());

        final List<Element> tableRowElements = tableRows.stream().skip(1).collect(Collectors.toList());

        tableRowElements.forEach(tableElement -> { // Serviços + status
            final Element authorizerTag = tableElement.children().select("td").first(); // System!

            String authorizerName;
            authorizerName = getElementName(authorizerTag);

            Authorizer authorizer = findAuthorizerByName(authorizerName);

            List<NfeServiceStatusEnum> nfeServiceStatusEnums = tableElement.children()
                                                                           .stream()
                                                                           .skip(1) // Should not be used with paralelism
                                                                           .map(Element::children)
                                                                           .map(newElement -> newElement.attr("src"))
                                                                           .map(this::parseStatus)
                                                                           .collect(Collectors.toList());

            for (int i = 0; i < nfeServiceStatusEnums.size(); i++)
            {
                NfeService nfeService = getServiceByName(services.get(i));
                NfeServiceHistory nfeServiceHistory = new NfeServiceHistory();

                nfeServiceHistory.setStatus(nfeServiceStatusEnums.get(i));
                nfeServiceHistory.setService(nfeService);
                nfeServiceHistory.setAuthorizer(authorizer);
                serviceHistories.add(nfeServiceHistory);
            }
        });

        logger.info("Entities successfully updated");
        nfeServiceHistoryRepository.saveAll(serviceHistories);
    }


    @Autowired
    public void setServiceHistoryRepository(NfeServiceHistoryRepository nfeServiceHistoryRepository)
    {
        this.nfeServiceHistoryRepository = nfeServiceHistoryRepository;
    }

    @Autowired
    public void setAuthorizerRepository(AuthorizerRepository authorizerRepository)
    {
        this.authorizerRepository = authorizerRepository;
    }

    @Autowired
    public void setServiceRepository(NfeServiceRepository nfeServiceRepository)
    {
        this.nfeServiceRepository = nfeServiceRepository;
    }

    private NfeService getServiceByName(String name)
    {
        NfeService nfeService = nfeServiceRepository.findByName(name);

        if (nfeService == null)
            throw new NullPointerException(String.format("No service exists with the name %s", name));

        return nfeService;
    }

    private String getElementName(Element authorizerTag)
    {
        if (authorizerTag != null)
            return authorizerTag.text();

        throw new NullPointerException("Unable to find system name.");
    }

    private Authorizer findAuthorizerByName(String name)
    {
        Authorizer authorizer = authorizerRepository.findByName(name);

        if (authorizer == null)
            throw new NullPointerException(
                    String.format("Authorizer must no be null. Failed to find entity for %s", name));
        return authorizer;
    }

    private Element getTabelaListagemDadosFromDocument(Document document)
    {
        Elements servicesTableElements = document.select(".tabelaListagemDados");

        if (servicesTableElements.size() > 1)
            throw new NonUniqueResultException();

        return servicesTableElements.first();
    }

    private NfeServiceStatusEnum parseStatus(String text)
    {
        List<TextMatchingValidator<NfeServiceStatusEnum>> validatorList = new ArrayList<>();

        Validator<NfeServiceStatusEnum> validator = new Validator<NfeServiceStatusEnum>()
                .setDefaultCase(SERVICE_NOT_REGISTERED);

        validatorList.add(new ServiceStatusValidator<>("verde", SERVICE_AVAILABLE));
        validatorList.add(new ServiceStatusValidator<>("vermelho", SERVICE_UNAVAILABLE));
        validatorList.add(new ServiceStatusValidator<>("amarelo", SERVICE_UNSTABLE));

        return validator.validate(validatorList, text);
    }
}
