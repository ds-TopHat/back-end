package com.mathfusion.domain.wrongnote.service;

import com.mathfusion.domain.wrongnote.dto.WrongNoteResponse;
import com.mathfusion.domain.wrongnote.repository.WrongNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WrongNoteService {

    private final WrongNoteRepository wrongNoteRepository;

    public List<WrongNoteResponse> getWrongNotes(String email) {
        return wrongNoteRepository.findWrongNotesByUserEmail(email);
    }
}
