package com.example.demo.Irangclass;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IrangclassDao extends JpaRepository<Irangclass, Integer> {

	Irangclass findByClassname(String classname);
}
