package com.example.integratebank.datafeed;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DatafeedRepository extends JpaRepository<Datafeed, Long> {
}
