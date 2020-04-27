package org.casadocodigo.loja.controllers;

import java.util.List;

import javax.validation.Valid;

import org.casadocodigo.loja.daos.ProdutoDAO;
import org.casadocodigo.loja.infra.FileSaver;
import org.casadocodigo.loja.model.Produto;
import org.casadocodigo.loja.model.TipoPreco;
import org.casadocodigo.loja.validation.ProdutoValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/produtos")
public class ProdutosController {
	
	@InitBinder
	public void initBinder(WebDataBinder binder){
	    binder.addValidators(new ProdutoValidation());
	}
	
	@Autowired
	private ProdutoDAO produtoDAO;
	
	@Autowired
	private FileSaver fileSaver;
	
	@RequestMapping("/form")
	public ModelAndView form(Produto produto) {
		ModelAndView mv = new ModelAndView("produtos/form");
		mv.addObject("tipos", TipoPreco.values());
		return mv;
	}
	
	@PostMapping
	public ModelAndView gravar(MultipartFile sumario, @Valid Produto produto, BindingResult result,
		RedirectAttributes redirectAttributes) {
		
		System.out.println(sumario.getOriginalFilename());
		
		if(result.hasErrors()) {
			return form(produto);
		}
		
		String path = fileSaver.write("arquivo-sumario", sumario);
		produto.setSumarioPath(path);
		
		produtoDAO.gravar(produto);
		
		redirectAttributes.addFlashAttribute("sucesso", "produto cadastrado com SUCESSO!");
		
		return  new ModelAndView("redirect:produtos");
	}
	
	@GetMapping
	public ModelAndView listar() {
		List<Produto> produtos = produtoDAO.listar();
		ModelAndView mv = new ModelAndView("produtos/lista");
		mv.addObject("produtos", produtos);
		return mv;
	}
}