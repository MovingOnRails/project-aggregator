package com.example.project_aggregator.Controller;

import com.example.project_aggregator.Dto.PageNumberDto;
import com.example.project_aggregator.entity.Item;
import com.example.project_aggregator.entity.Photo;
import com.example.project_aggregator.repository.ItemRepository;
import com.example.project_aggregator.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;

@RestController
public class MainController {


    @Autowired
    ItemRepository itemRepository;

    private final PhotoService photoService;

    @Autowired
    public MainController(PhotoService photoService){
        this.photoService = photoService;
    }

    private List<PageNumberDto> makePageNumberDtoList(int numberOfPages){
        List<PageNumberDto> res = new ArrayList<>();
        for(int i=1;i<=numberOfPages;i++){
            PageNumberDto pageDto = new PageNumberDto();
            pageDto.number = i;
            pageDto.redirectUrl = "mainPage?page="+i;
            res.add(pageDto);
        }
        return res;
    }

    @GetMapping("/mainPage")
    public ModelAndView mainPage(@RequestParam Integer page, Model model){
        // page is just the page number
        int pageIndex = page-1;
        int pageSize = 3;
        Pageable pageWithThreeElements = PageRequest.of(pageIndex,pageSize);
        Page<Item> all_items = itemRepository.findAll(pageWithThreeElements);

        // Calculate the number of pages to make the bootstrap page selector
        int allItemsSize = itemRepository.findAll().size();
        int numberOfPages = (int) Math.floor(allItemsSize/pageSize);
        if (allItemsSize % pageSize != 0){
            numberOfPages += 1;
        }
        List<PageNumberDto> pageNumbers = makePageNumberDtoList(numberOfPages);

        Photo test = this.photoService.retrievePhotoForItem(all_items.getContent().get(0));

        ModelAndView mav = new ModelAndView();
        mav.setViewName("mainPage");
        model.addAttribute("Title", "Marinabi");
        model.addAttribute("Sidebar_desc", "Marinabi");
        model.addAttribute("PhotoTitle", "Photo X");
        model.addAttribute("PhotoBase64", "data:image/jpeg;base64,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");
        model.addAttribute("PhotoDescription", "Description changed");
        model.addAttribute("Items", all_items);
        model.addAttribute("PageNumbers", pageNumbers);
        return mav;
    }


    @GetMapping("/")
    public RedirectView redirect(){
        String url = "/mainPage?page=1";
        return new RedirectView(url);
    }

    @GetMapping("/login")
    public ModelAndView login(Model model){

        ModelAndView mav = new ModelAndView();
        mav.setViewName("loginPage");
        model.addAttribute("Test", "thymeleaf replace");
        return mav;
    }

    @GetMapping("/headertemplate")
    public ModelAndView headertemplate(Model model){
        ModelAndView mav = new ModelAndView();
        mav.setViewName("HeaderAndSidebarTemplate");
        model.addAttribute("Test", "thymeleaf replace");
        return mav;
    }
}
