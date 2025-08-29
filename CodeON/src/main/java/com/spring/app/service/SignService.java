package com.spring.app.service;

import org.springframework.ui.Model;

public interface SignService {

	void exportDraftToExcel(Long draftSeq, Model model);

}
