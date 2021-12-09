package dev.heizer.nfewebscrapingrestapi.resources;

import dev.heizer.nfewebscrapingrestapi.models.NfeServiceHistory;
import dev.heizer.nfewebscrapingrestapi.models.NfeState;
import dev.heizer.nfewebscrapingrestapi.repositories.AuthorizerRepository;
import dev.heizer.nfewebscrapingrestapi.repositories.NfeServiceHistoryRepository;
import dev.heizer.nfewebscrapingrestapi.repositories.NfeStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static dev.heizer.nfewebscrapingrestapi.util.TimestampCalculator.now;
import static dev.heizer.nfewebscrapingrestapi.util.TimestampCalculator.timeWalk;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/services")
public class NfeServiceController
{
    private final NfeServiceHistoryRepository nfeServiceHistoryRepository;
    private final NfeStateRepository nfeStateRepository;
    private final AuthorizerRepository authorizerRepository;

    @Autowired
    Integer schedulerTime;

    @Autowired
    TimeUnit schedulerTimeTimeUnit;

    public NfeServiceController(NfeServiceHistoryRepository nfeServiceHistoryRepository,
                                NfeStateRepository nfeStateRepository,
                                AuthorizerRepository authorizerRepository)
    {
        this.nfeServiceHistoryRepository = nfeServiceHistoryRepository;
        this.nfeStateRepository = nfeStateRepository;
        this.authorizerRepository = authorizerRepository;
    }

    @GetMapping
    public HttpEntity<CollectionModel<NfeServiceHistory>> findAllEntries()
    {
        List<NfeServiceHistory> models = nfeServiceHistoryRepository.findAll();

        models.forEach(model -> {
            model.add(
                    linkTo(methodOn(NfeServiceController.class).findAllEntries())
                            .slash(model.getId())
                            .withSelfRel());
        });
        CollectionModel<NfeServiceHistory> collectionModel =
                CollectionModel.of(models)
                               .add(linkTo(methodOn(NfeServiceController.class)
                                       .findAllEntries())
                                       .withSelfRel());

        return new ResponseEntity<>(collectionModel, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public HttpEntity<NfeServiceHistory> findEntry(@PathVariable Long id)
    {
        NfeServiceHistory model = nfeServiceHistoryRepository.findById(id)
                                                             .orElseThrow();

        model.add(linkTo(methodOn(NfeServiceController.class)
                .findEntry(id))
                .withSelfRel());

        return new ResponseEntity<>(model, HttpStatus.OK);
    }

    @GetMapping(params = "autorizador")
    public HttpEntity<CollectionModel<NfeServiceHistory>> findAllEntriesByState(
            @RequestParam("autorizador") String authorizer)
    {
        List<NfeServiceHistory> serviceHistoriesByState =
                nfeServiceHistoryRepository.findAllByAuthorizer_name(authorizer);

        serviceHistoriesByState.forEach(model -> {
            model.add(linkTo(methodOn(NfeServiceController.class).findAllEntriesByState(authorizer))
                    .slash(model.getId())
                    .withSelfRel());
        });

        CollectionModel<NfeServiceHistory> collectionModel = CollectionModel.of(serviceHistoriesByState);

        collectionModel
                .add(linkTo(methodOn(NfeServiceController.class).findAllEntriesByState(authorizer))
                        .withSelfRel());

        return new ResponseEntity<>(collectionModel, HttpStatus.OK);
    }

    @GetMapping("/last")
    public HttpEntity<CollectionModel<NfeServiceHistory>> findLastByStateName(@RequestParam String estado)
    {
        List<NfeState> nfeStates = nfeStateRepository.findAllByName(estado.toUpperCase(Locale.ROOT));
        List<NfeServiceHistory> nfeServiceHistories = new ArrayList<>();

        Timestamp timestamp = timeWalk(now(), schedulerTime, schedulerTimeTimeUnit);

        nfeStates.forEach(state -> {
            authorizerRepository.findByState_Id(state.getId())
                                .forEach(authorizer ->
                                {
                                    nfeServiceHistoryRepository
                                            .findAllByTimestampGreaterThanAndAuthorizer_Id(timestamp, authorizer.getId())
                                            .forEach(nfeServiceHistory ->
                                            {
                                                nfeServiceHistories.add(nfeServiceHistory);

                                                nfeServiceHistory
                                                        .add(linkTo(methodOn(NfeServiceController.class)
                                                                .findLastByStateName(estado))
                                                                .withSelfRel());
                                            });
                                });
        });

        CollectionModel<NfeServiceHistory> nfeServiceHistoryCollectionModel = CollectionModel.of(nfeServiceHistories);
        return new ResponseEntity<>(nfeServiceHistoryCollectionModel, HttpStatus.OK);

        // TODO: Não pode ser pelo TopByAuthorizer. Tem que buscar pelas últimas ocorrências!
    }
}
