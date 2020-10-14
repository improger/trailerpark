package com.example.demo.repo;


import com.example.demo.models.LoginUser;
import com.example.demo.models.Trailer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import sun.rmi.runtime.Log;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<LoginUser, Long>, PagingAndSortingRepository<LoginUser, Long>, JpaRepository<LoginUser, Long> {

    //LoginUser findByUsername(String username);

    LoginUser findByIdAndUsername(Long id, String username);

    Optional<LoginUser> findByUsername(String username);

}