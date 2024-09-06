package com.example.integratebank.datafeed;

import com.example.integratebank.dto.DatafeedDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatafeedServiceImpl implements DatafeedService {

    private final DatafeedRepository datafeedRepository;

    @Override
    public void addDataFeed(DatafeedDTO datafeedDTO) {
        try {
            datafeedRepository.save(new Datafeed(datafeedDTO));
        } catch (Exception e) {
            log.error("Error insert DataFeedEntity", e);
        }
    }
}
