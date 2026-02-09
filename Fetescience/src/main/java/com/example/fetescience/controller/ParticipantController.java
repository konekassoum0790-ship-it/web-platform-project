package com.example.fetescience.controller;

import com.example.fetescience.model.*;
import com.example.fetescience.repository.*;
import com.example.fetescience.service.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/participant")
@PreAuthorize("hasAnyRole('PARTICIPANT', 'ADMIN')")
public class ParticipantController {

    private final InscriptionService inscriptionService;
    private final PersonneRepository personneRepository;

    public ParticipantController(InscriptionService inscriptionService,
                                 PersonneRepository personneRepository) {
        this.inscriptionService = inscriptionService;
        this.personneRepository = personneRepository;
    }

    /*
    The previous code isnt useful
     */
}