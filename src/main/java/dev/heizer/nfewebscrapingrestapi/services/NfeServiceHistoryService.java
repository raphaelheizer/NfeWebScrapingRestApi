package dev.heizer.nfewebscrapingrestapi.services;

import dev.heizer.nfewebscrapingrestapi.models.Authorizer;
import dev.heizer.nfewebscrapingrestapi.models.NfeServiceCountDTO;
import dev.heizer.nfewebscrapingrestapi.models.NfeServiceHistory;
import dev.heizer.nfewebscrapingrestapi.models.NfeServiceStatusEnum;
import dev.heizer.nfewebscrapingrestapi.repositories.AuthorizerRepository;
import dev.heizer.nfewebscrapingrestapi.repositories.NfeServiceHistoryRepository;
import dev.heizer.nfewebscrapingrestapi.repositories.NfeStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static dev.heizer.nfewebscrapingrestapi.util.TimestampCalculator.now;
import static dev.heizer.nfewebscrapingrestapi.util.TimestampCalculator.timeWalk;

@Service
public class NfeServiceHistoryService
{
    private final NfeServiceHistoryRepository nfeServiceHistoryRepository;
    private final NfeStateRepository nfeStateRepository;
    private final AuthorizerRepository authorizerRepository;

    private Integer schedulerTime;
    private TimeUnit schedulerTimeTimeUnit;

    public NfeServiceHistoryService(NfeServiceHistoryRepository nfeServiceHistoryRepository,
                                    NfeStateRepository nfeStateRepository,
                                    AuthorizerRepository authorizerRepository)
    {
        this.nfeServiceHistoryRepository = nfeServiceHistoryRepository;
        this.nfeStateRepository = nfeStateRepository;
        this.authorizerRepository = authorizerRepository;
    }

    private List<Authorizer> findAllAuthorizersByStateName(String state)
    {
        return nfeStateRepository.findAllByName(state.toUpperCase(Locale.ROOT))
                                 .stream()
                                 .map(nfeState -> authorizerRepository.findByState_Id(nfeState.getId()))
                                 .flatMap(Collection::stream)
                                 .collect(Collectors.toList());
    }


    /**
     * Finds all occurrences of {@link NfeServiceHistory} filtered by state name
     * @param estado State name to filter by
     * @return {@link List<NfeServiceHistory>}
     */
    public List<NfeServiceHistory> findAllLastByStateName(String estado)
    {
        Timestamp timestamp = timeWalk(now(), schedulerTime, schedulerTimeTimeUnit);
        List<Authorizer> authorizers = findAllAuthorizersByStateName(estado);

        return authorizers
                .stream()
                .map(authorizer ->
                        nfeServiceHistoryRepository
                                .findAllByTimestampGreaterThanAndAuthorizer_Id(timestamp, authorizer.getId()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public List<NfeServiceHistory> findLastByStateName(String estado)
    {
        return findAllAuthorizersByStateName(estado)
                .stream().map(authorizer -> nfeServiceHistoryRepository.findAllByAuthorizer_name(authorizer.getName()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * Finds all occurrences of {@link NfeServiceHistory} filtered by state name and service name
     * @param name service name to filter by
     * @param state state name to filter by
     * @return {@link List<NfeServiceHistory>}
     */
    public List<NfeServiceHistory> findLastByNameAndStateName(String name, String state)
    {
        List<NfeServiceHistory> nfeServiceHistories = findAllLastByStateName(state);

        return nfeServiceHistories.stream()
                                  .filter(nfeServiceHistory ->
                                          nfeServiceHistory.getService()
                                                           .getName()
                                                           .equals(name))
                                  .collect(Collectors.toList());
    }

    /**
     * Finds all occurrences of {@link NfeServiceHistory} filtered by state name and service name
     * @param state state name to filter by
     @return {@link List<NfeServiceHistory>}
     */
    public List<NfeServiceHistory> findAllBetweenTimestampAndStateName(Timestamp initialTime, Timestamp finalTime,
                                                                       String state)
    {
        List<Authorizer> authorizers = findAllAuthorizersByStateName(state);

        return authorizers.stream()
                          .map(authorizer ->
                                  nfeServiceHistoryRepository
                                          .findAllByTimestampBetweenAndAuthorizer_Id(initialTime, finalTime, authorizer.getId()))
                          .flatMap(Collection::stream)
                          .collect(Collectors.toList());
    }

    /**
     * Counts all occurrences of {@link NfeServiceHistory} filtered by state
     * @return {@link List<NfeServiceCountDTO>}
     */
    public List<NfeServiceCountDTO> countMostUnavailableStatus()
    {
        List<NfeServiceCountDTO> nfeServiceCountDTOS = new ArrayList<>();


        nfeStateRepository.findDistinctNames()
                          .forEach(nfeState -> {
                              NfeServiceCountDTO nfeServiceCountDTO = new NfeServiceCountDTO();

                              nfeServiceCountDTO.setStateName(nfeState.getName());
                              nfeServiceCountDTO.setAuthorizers(authorizerRepository.findByState_Id(nfeState.getId()));

                              nfeServiceCountDTO.getAuthorizers().forEach(authorizer -> {

                                  nfeServiceCountDTO.incrementCount(
                                          nfeServiceHistoryRepository.countAllByStatusAndAuthorizer_Id(
                                                  NfeServiceStatusEnum.SERVICE_UNAVAILABLE, authorizer.getId()));
                              });

                              nfeServiceCountDTOS.add(nfeServiceCountDTO);
                          });

        Long largestValue = nfeServiceCountDTOS.
                stream()
                .max(Comparator.comparing(NfeServiceCountDTO::getCount))
                .map(NfeServiceCountDTO::getCount)
                .orElseThrow();

        return nfeServiceCountDTOS.stream().filter(nfeServiceCountDTO -> nfeServiceCountDTO.getCount() == largestValue)
                                  .filter(nfeServiceCountDTO -> nfeServiceCountDTO.getCount() != 0)
                                  .collect(Collectors.toList());
    }

    @Autowired
    public void setSchedulerTime(Integer schedulerTime)
    {this.schedulerTime = schedulerTime;}

    @Autowired
    public void setSchedulerTimeTimeUnit(TimeUnit schedulerTimeTimeUnit)
    {this.schedulerTimeTimeUnit = schedulerTimeTimeUnit;}


}
