package com.example.demo.childlog;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.chcomment.ChcommentDto;
import com.example.demo.chcomment.ChcommentService;
import com.example.demo.child.ChildDto;
import com.example.demo.child.ChildService;


@Controller
@RequestMapping("/childlog")
public class ChildlogController {
	@Autowired
	private ChildlogService service;
	
	@Autowired
	private ChcommentService servcom;
	
//	@Autowired
//	private ChildService servchild;
	
	
	@Value("${spring.servlet.multipart.location}") 
	private String path;
	
	// 글작성폼
	@GetMapping("/add")
	public String addForm(ModelMap map) {
		//map.addAttribute("teacher", map)
		map.addAttribute("bodyview", "/WEB-INF/views/childlog/add.jsp");
		return "index";
	}
	
	// 글 작성 완료
	@PostMapping("/add")
	public String add(ChildlogDto dto) {
		MultipartFile f = dto.getF();
		String fname = f.getOriginalFilename();
		File f2 = new File(path+fname);
		try {
			f.transferTo(f2);
		} catch (IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dto.setImg(fname);
		service.save(dto);
		return "redirect:/childlog/list?childid=" + dto.getChildid();
	}
	
	// 리스트 보여주기 --> childid & 날짜로 검색 
	@GetMapping("/list")
	public String list(ModelMap map, String childid) {
		ArrayList<ChildlogDto> list = service.getByChildid(childid);
		map.addAttribute("childid", childid);
		map.addAttribute("list", list);
		map.addAttribute("bodyview", "/WEB-INF/views/childlog/list.jsp");
		return "index";
	}
	
	// (리스트) 월별 보기 
	
	// (리스트) 날짜 보기
	
	// (리스트) 미확인 보기 
	@GetMapping("/unchecked")
	public String uncheckedlist(ModelMap map, String childid) {
		ArrayList<ChildlogDto> list = service.getByTcheck(0);
		map.addAttribute("childid", childid);
		map.addAttribute("list", list);
		map.addAttribute("bodyview", "/WEB-INF/views/childlog/list.jsp");
		return "index";
	}
	
	
	
	// 디테일 보여주기
	@GetMapping("/detail")
	public String detail(int chlognum, ModelMap map) {
		ChildlogDto dto = service.getChlog(chlognum);
		ChcommentDto comdto = servcom.get(chlognum);
//		String childid = new StringBuilder().append(dto.getChildid()).toString();
//		ChildDto chdto = servchild.getById(childid);
//		map.addAttribute("child", chdto);
		map.addAttribute("com", comdto);
		map.addAttribute("dto", dto);
		map.addAttribute("bodyview", "/WEB-INF/views/childlog/detail.jsp");
		return "index";
	}
	
	@GetMapping("/read_img")
	public ResponseEntity<byte[]> read_img(String fname) {
		File f = new File(path + fname);
		HttpHeaders header = new HttpHeaders(); // HttpHeaders 객체 생성
		ResponseEntity<byte[]> result = null; // 선언
		try {
			header.add("Content-Type", Files.probeContentType(f.toPath()));// 응답 데이터의 종류를 설정
			// 응답 객체 생성
			result = new ResponseEntity<byte[]>(FileCopyUtils.copyToByteArray(f), header, HttpStatus.OK);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	// 수정폼 띄우기
	@GetMapping("/edit")
	public String editform(ModelMap map, int chlognum) {
		ChildlogDto dto = service.getChlog(chlognum);
		map.addAttribute("dto", dto);
		map.addAttribute("bodyview", "/WEB-INF/views/childlog/edit.jsp");
		return "index";
	}
	
	
	// 수정 (이미지, 내용)
	@PostMapping("/edit")
	public String edit(ModelMap map, ChildlogDto dto, MultipartFile f1) {
		ChildlogDto dto1 = service.getChlog(dto.getChlognum());
		dto1.setContent(dto.getContent());
		
		String fname = f1.getOriginalFilename();
		String newpath = "";
		if (fname != null && !fname.equals("")) {
			newpath = path + fname;
			File newfile = new File(newpath);// 복사할 새 파일 생성. 
			try {
				f1.transferTo(newfile);// 파일 업로드
				String delf = ""; // 삭제할 파일 경로
				delf = dto1.getImg(); // 기존파일을 삭제경로에 담기 
				dto1.setImg(newpath); // 새 파일 경로로 변수값 변경 
				if (delf != null) {
					File delFile = new File(delf); // 그 경로에 있는 파일 객체 가져오기 
					delFile.delete();
				}
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		int chlognum = service.save(dto1);
		
		return "redirect:/childlog/detail?chlognum=" + chlognum;
	}
	
	
	// 삭제 (이미지)
//	@GetMapping("/delimg")
//	public String delimg(int num) {
//		ChildlogDto dto = service.getChlog(num);
//		String delf = "";
//			delf = dto.getImg();
//			dto.setImg(null);
//		if (delf != null) {
//			File delFile = new File(delf);
//			delFile.delete();
//		}
//		service.save(dto);
//		return "redirect:/childlog/detail?num=" + num;
//	}
	
	// 삭제
	@GetMapping("/del")
	public String del(int chlognum, String childid) {
		ChildlogDto dto = service.getChlog(chlognum);
		String delPath = path + dto.getImg();
		File delFile = new File(delPath);
		delFile.delete(); // 저장된 파일 삭제
		service.delChlog(chlognum); // DB 삭제
		return "redirect:/childlog/list?childid="+childid;
	}
	
	// 선생님이 확인 후 체크하기 -- 체크(1)하면 true(checked)
	@ResponseBody
	@GetMapping("/tcheck")
	public Map tcheck(int chlognum) {
		ChildlogDto dto = service.getChlog(chlognum);
		boolean flag = false;
		dto.setTcheck(1);
		service.save(dto);
		dto = service.getChlog(chlognum);
		if (dto.getTcheck() == 1) {
			flag = true;
		}
		Map map = new HashMap(); // Map 하나 당 {}
		map.put("flag", flag); // {"flag", true(or false)}
		return map;
	}
	

}
