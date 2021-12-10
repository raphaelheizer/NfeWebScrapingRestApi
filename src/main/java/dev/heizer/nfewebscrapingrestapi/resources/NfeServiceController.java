package dev.heizer.nfewebscrapingrestapi.resources;

import dev.heizer.nfewebscrapingrestapi.exceptions.WrongDateFormatException;
import dev.heizer.nfewebscrapingrestapi.models.NfeServiceCountDTO;
import dev.heizer.nfewebscrapingrestapi.models.NfeServiceHistory;
import dev.heizer.nfewebscrapingrestapi.services.NfeServiceHistoryService;
import dev.heizer.nfewebscrapingrestapi.util.TimestampCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/services")
public class NfeServiceController
{
    private static final Logger logger = LoggerFactory.getLogger(NfeServiceController.class);

    private final NfeServiceHistoryService nfeServiceHistoryService;

    public NfeServiceController(NfeServiceHistoryService nfeServiceHistoryService)
    {
        this.nfeServiceHistoryService = nfeServiceHistoryService;
    }

    @GetMapping("/now")
    public HttpEntity<CollectionModel<NfeServiceHistory>> getAllLastByStateName(@RequestParam String estado)
    {
        List<NfeServiceHistory> nfeServiceHistories = nfeServiceHistoryService.findAllLastByStateName(estado);

        CollectionModel<NfeServiceHistory> nfeServiceHistoryCollectionModel = CollectionModel.of(nfeServiceHistories);

        nfeServiceHistories
                .forEach(nfeServiceHistory -> {
                    nfeServiceHistory.add(linkTo(methodOn(NfeServiceController.class)
                            .getAllLastByStateName(estado))
                            .withSelfRel());
                });

        nfeServiceHistoryCollectionModel
                .add(linkTo(methodOn(NfeServiceController.class)
                        .getAllLastByStateName(estado))
                        .withSelfRel());

        return new ResponseEntity<>(nfeServiceHistoryCollectionModel, HttpStatus.OK);
    }

    @GetMapping(value = "/now", params = "estado")
    public HttpEntity<CollectionModel<NfeServiceHistory>> getByState(@RequestParam String estado)
    {
        List<NfeServiceHistory> nfeServiceHistory =
                nfeServiceHistoryService.findLastByStateName(estado);

        nfeServiceHistory
                .forEach(nfeServiceHistoryIteration ->
                        nfeServiceHistoryIteration.add(linkTo(methodOn(NfeServiceController.class)
                                .getByState(estado))
                                .withSelfRel()));

        final CollectionModel<NfeServiceHistory> nfeServiceHistoryCollectionModel =
                CollectionModel.of(nfeServiceHistory);

        nfeServiceHistoryCollectionModel.add(linkTo(methodOn(NfeServiceController.class)
                .getByState(estado))
                .withSelfRel());

        return new ResponseEntity<>(nfeServiceHistoryCollectionModel, HttpStatus.OK);
    }

    @GetMapping(value = "/now", params = {"estado", "servico"})
    public HttpEntity<CollectionModel<NfeServiceHistory>> getLastByStateName(@RequestParam String estado,
                                                                             @RequestParam String servico)
    {
        List<NfeServiceHistory> nfeServiceHistory =
                nfeServiceHistoryService.findLastByNameAndStateName(servico, estado);

        CollectionModel<NfeServiceHistory> nfeServiceHistoryCollectionModel = CollectionModel.of(nfeServiceHistory);

        nfeServiceHistoryCollectionModel
                .add(linkTo(methodOn(NfeServiceController.class)
                        .getLastByStateName(estado, servico)).withSelfRel());

        return new ResponseEntity<>(nfeServiceHistoryCollectionModel, HttpStatus.OK);
    }

    @GetMapping(params = {"estado", "inicio", "fim"})
    public HttpEntity<CollectionModel<NfeServiceHistory>> getByTimestampBetweenAndStateName(
            @RequestParam String inicio,
            @RequestParam String fim,
            @RequestParam String estado)
    {
        final String dateFormat = "dd/MM/yyyy";

        Date inicioDate;
        Date fimDate;

        try
        {
            inicioDate = new SimpleDateFormat(dateFormat).parse(inicio);
            fimDate = new SimpleDateFormat(dateFormat).parse(fim);
        }
        catch (ParseException parseException)
        {
            throw new WrongDateFormatException(String.format("Incorrect date format. Should be dd/MM/yyy, found %s " +
                    "and %s", inicio, fim), parseException);
        }

        List<NfeServiceHistory> nfeServiceHistories =
                nfeServiceHistoryService.findAllBetweenTimestampAndStateName(new Timestamp(inicioDate.getTime()),
                        TimestampCalculator.endOfDay(fimDate), estado);

        CollectionModel<NfeServiceHistory> nfeServiceHistoryCollectionModel = CollectionModel.of(nfeServiceHistories);

        nfeServiceHistoryCollectionModel.add(linkTo(methodOn(NfeServiceController.class)
                .getByTimestampBetweenAndStateName(inicio, fim, estado))
                .withSelfRel());

        return new ResponseEntity<>(nfeServiceHistoryCollectionModel, HttpStatus.OK);
    }

    @GetMapping(value = "countUnavailable")
    public HttpEntity<CollectionModel<NfeServiceCountDTO>> getMostUnavailableState()
    {
        final List<NfeServiceCountDTO> nfeServiceCountDTOS = nfeServiceHistoryService.countMostUnavailableStatus();

        nfeServiceCountDTOS.forEach(nfeServiceCountDTO -> {
            nfeServiceCountDTO.add(linkTo(methodOn(NfeServiceController.class)
                    .getByState(nfeServiceCountDTO.getStateName())).withSelfRel());
        });

        final CollectionModel<NfeServiceCountDTO> nfeServiceCountDTOCollectionModel =
                CollectionModel.of(nfeServiceCountDTOS);

        nfeServiceCountDTOCollectionModel
                .add(linkTo(methodOn(NfeServiceController.class)
                        .getMostUnavailableState())
                        .withSelfRel());

        return new ResponseEntity<>(nfeServiceCountDTOCollectionModel, HttpStatus.OK);
    }

}
