package com.example.firstproject.controller;

import com.example.firstproject.dto.ArticleForm;
import com.example.firstproject.entity.Article;
import com.example.firstproject.repository.ArticleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@Slf4j // 로깅을 위한 골뱅이다
public class ArticleController {
    @Autowired // 스프링 부트가 미리 생성해놓은 객체를 가져다가 연결
    private ArticleRepository articleRepository;

    @GetMapping("/articles/new")
    public String newArticleForm() {
        // 이 메소드는 화면단을 가져오기위한 get 메소드
        // 새로운 데이터를 만드는 폼
        return "articles/new";
    }

    @PostMapping("/articles/create")
    public String createArticles(ArticleForm form) {
        log.info(form.toString());

        // 브라우저에서 던진 post 가 위 dto 로 받아진다.
        // 이 메소드는 인풋 데이터를 서버측으로 넘겨주기위한 post 메소드다.
        // ...//articles/create&name=""?title=""
        // 웹브라우저에서 받은 데이터를 db(현재는 h2를 사용)에 저장

        // 1. DTO 를 entity 로 변환해야함.
        // 2. 레포지토리에게 엔티디를 디비안에 저장!

        Article article = form.toEntity();
        // 디비에 저장하기위한 엔티티
        log.info(article.toString());
        // 로깅이란? 자동차에서 블박이랑 비슷함. 서버에서 일어나는 일들을 모두 기록해줌.


        Article saved = articleRepository.save(article);
        // 디비에 저장시켜줌
        log.info(saved.toString());

        // 그러면 만들었으면 반환값을 머스태치 화면이 아니라 객체를 던져주면 받는건가??
        return "redirect:/articles/" + saved.getId();
    }

    // 밑에 id 는 어떻게 가져오나? PathVariable 로 받는다.
    @GetMapping("/articles/{id}")
    public String show(@PathVariable Long id , Model model) {
        log.info("id: " + id);

        // id 로 데이터를 가져옴
        Article articleEntity = articleRepository.findById(id).orElse(null);
        // >> 아이디를 찾았는데 아이디가 없다? 널을 반환해라

        // 가져온 데이터를 머스테치에 보여주기위해 모델에 등록!
        model.addAttribute("article" , articleEntity);
        // 모델에 이름을 article 이라고 등록을 한거고 디비에서 가져온 아티클 엔티티가 넘어가게 된다.

        // 보여줄 페이지를 설정

        // 머스테치의 이름 이다.
        return "articles/show";
    }

    @GetMapping("/articles")
    public  String index (Model model) {
        // 1: 모든 아티클스를 가져온다. 리파지토리가 필요하다.
       List<Article> articles = articleRepository.findAll();
       // 파인드올 하여 받아낼때는 레파지토리에서 파인드올을 오버라이드하여 가져온다.
        // List > ArrayList 타입으로 형변환이 가능하다.

        // 2. 가져온 아티클 묶음을 뷰단(머스태치 뷰)으로 전달!
        model.addAttribute( "articleList", articles);

        // 3. 뷰페이지 설정!

        return  "articles/index";
    }

    @GetMapping("/articles/{id}/edit")
    public String edit(@PathVariable Long id , Model model) {
        // PathVariable 는 위 겟 매핑에서 받는거랑 파라메터의 id 랑 같아야한다.
        // 수정할 데이터를 가져오기. 디비에서 꺼내옴.
       Article articleEntity = articleRepository.findById(id).orElse(null);
       // 디비에서 꺼내와서 뷰에 뿌려주기위해 에드어트리뷰트로 등록해준다.
       model.addAttribute("article" ,articleEntity);

       return "articles/edit";
    }

    @PostMapping("/articles/update")
    public String update(ArticleForm form) {
        // 받아온 form(DTO) 를 디비에 patch 시키기위해 entity로 변환한다.
       Article entity = form.toEntity();

       // 수정은 읽는것을 바꾸는것.
        // 디비의 기존데이터를 가져온다.
        Article target = articleRepository.findById(entity.getId()).orElse(null); // 엔티티에서 가져온다.

        // 기존 데이터의 값을 갱신(수정)한다.

        if(target != null) {
            articleRepository.save(entity);
            // 갱신
        }


       return "redirect:/articles/" + entity.getId();
    }

    @GetMapping("/articles/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes attributes) {
        log.info("delete request");
        // 결과값을 돌려준다.

        // 삭제 대상을 가져온다
       Article target = articleRepository.findById(id).orElse(null);

        // 대상을 삭제한다.
       if(target != null) {
           articleRepository.delete(target);
           attributes.addFlashAttribute("msg" , "delete!!"); // 프론트에 메세지 출력하기
       }

       return "redirect:/articles/index";
    }
}
