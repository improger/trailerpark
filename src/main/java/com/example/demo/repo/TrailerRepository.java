package com.example.demo.repo;

import com.example.demo.models.Trailer;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;


public interface TrailerRepository extends CrudRepository<Trailer, Long>, PagingAndSortingRepository<Trailer, Long>, JpaRepository <Trailer, Long> {

List<Trailer> findTop100ByOutTrcNotNull(Sort inDate);

List<Trailer> findByOutTrcIsNull(Sort inDate);

List<Trailer> findTop1000ByInDateBetweenOrderByInDateDesc(Date inDate, Date outDate);



List<Trailer> findByOutTrcIsNullAndTrl(String Trl);

List<Trailer> findByOutTrcAndId(String outTrc, Long id);

Trailer findByTrlAndId(String trl, Long id);



}
