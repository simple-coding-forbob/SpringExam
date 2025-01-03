/**
 * 
 */
package egovframework.example.controller.advanced;

import java.util.List;

import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import egovframework.example.service.advanced.GalleryService;
import egovframework.example.vo.advanced.FileDbVO;
import egovframework.example.vo.advanced.GalleryVO;
import egovframework.example.vo.common.Criteria;

@Controller
public class GalleryController {

	@Autowired
	GalleryService galleryService; 

	@GetMapping("/advanced/gallery.do")
	public String selectGalleryList(@ModelAttribute("searchVO") Criteria searchVO, Model model) throws Exception {
		searchVO.setPageUnit(3); 
		searchVO.setPageSize(2); 

		PaginationInfo paginationInfo = new PaginationInfo(); 
		paginationInfo.setCurrentPageNo(searchVO.getPageIndex()); 
		paginationInfo.setRecordCountPerPage(searchVO.getPageUnit()); 
		paginationInfo.setPageSize(searchVO.getPageSize()); 

		searchVO.setFirstIndex(paginationInfo.getFirstRecordIndex()); 
		searchVO.setLastIndex(paginationInfo.getLastRecordIndex()); 
		searchVO.setRecordCountPerPage(paginationInfo.getRecordCountPerPage()); 

		List<?> gallerys = galleryService.selectGalleryList(searchVO);
		model.addAttribute("gallerys", gallerys);
		
		int totCnt = galleryService.selectGalleryListTotCnt(searchVO);
		
		paginationInfo.setTotalRecordCount(totCnt);
		model.addAttribute("paginationInfo", paginationInfo);
		
		return "advanced/gallery/gallery_all";
	}
	
	@GetMapping("/advanced/gallery/addition.do")
	public String createGalleryView() {
		return "advanced/gallery/add_gallery";
	}
	
	@PostMapping("/advanced/gallery/add.do")
	public String insert(@RequestParam(defaultValue = "") String galleryTitle,
							@RequestParam(required = false) MultipartFile image
			) throws Exception {
		GalleryVO galleryVO = new GalleryVO(galleryTitle, image.getBytes());
		galleryService.insert(galleryVO);
		return "redirect:/advanced/gallery.do"; 
	}
	
	@GetMapping("/advanced/gallery/download.do")
	@ResponseBody
	public ResponseEntity<byte[]> findDownload(@RequestParam(defaultValue = "") String uuid) throws Exception {
		GalleryVO galleryVO = galleryService.selectGallery(uuid);
		
        HttpHeaders headers = new HttpHeaders();             
        headers.setContentDispositionFormData("attachment", galleryVO.getUuid()); 
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);              

        return new ResponseEntity<byte[]>(galleryVO.getGalleryData(), headers, HttpStatus.OK);
	}
	
	@PostMapping("/advanced/gallery/delete.do")
	public String delete(@RequestParam(defaultValue = "") String uuid) throws Exception {
		galleryService.delete(uuid);
		return "redirect:/advanced/gallery.do";
	}
}
