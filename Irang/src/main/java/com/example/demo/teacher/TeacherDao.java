package com.example.demo.teacher;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Irangclass.Irangclass;

@Repository
public interface TeacherDao extends JpaRepository<Teacher, String> {

	ArrayList<Teacher> findByNameLike(String name);
	//CLAss 번호로찾기 
	ArrayList<Teacher> findByClassnum(Irangclass classnum);
	
}
