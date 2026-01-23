package com.example.online_learning.serviceImpl;

import com.example.online_learning.dto.request.QuestionDtoReq;
import com.example.online_learning.dto.response.ExcelErrorDto;
import com.example.online_learning.dto.response.ExcelUploadResponseDto;
import com.example.online_learning.dto.response.QuestionDtoRes;
import com.example.online_learning.entity.Assignment;
import com.example.online_learning.entity.Question;
import com.example.online_learning.exception.NotFoundException;
import com.example.online_learning.repository.AssignmentRepository;
import com.example.online_learning.repository.QuestionRepository;
import com.example.online_learning.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final AssignmentRepository assignmentRepository;
    private static final int MAX_QUESTIONS = 100;
    private static final int MIN_QUESTIONS_REQUIRED = 25; 
    private static final int QUESTIONS_TO_SELECT = 20; 
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; 

    @Override
    public ExcelUploadResponseDto uploadQuestionsFromExcel(Long assignmentId, MultipartFile file) {

        validateFile(file);

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Assignment not found with id: " + assignmentId));

      
        List<QuestionDtoReq> questions = parseExcelFile(file);
        
        List<ExcelErrorDto> errors = new ArrayList<>();
        List<Question> validQuestions = new ArrayList<>();
        
        int rowNumber = 2; 
        for (QuestionDtoReq questionDto : questions) {
            try {
                validateQuestionData(questionDto);
                Question question = Question.builder()
                        .assignment(assignment)
                        .questionText(questionDto.getQuestionText())
                        .optionA(questionDto.getOptionA())
                        .optionB(questionDto.getOptionB())
                        .optionC(questionDto.getOptionC())
                        .optionD(questionDto.getOptionD())
                        .correctAnswer(questionDto.getCorrectAnswer().toUpperCase().trim())
                        .orderIndex(questionDto.getOrderIndex())
                        .points(questionDto.getPoints())
                        .build();
                validQuestions.add(question);
            } catch (Exception e) {
                errors.add(ExcelErrorDto.builder()
                        .row(rowNumber)
                        .message(e.getMessage())
                        .build());
            }
            rowNumber++;
        }

  
        if (validQuestions.size() < MIN_QUESTIONS_REQUIRED) {
            throw new IllegalArgumentException(
                    String.format("File must contain at least %d valid questions. Found only %d questions.", 
                            MIN_QUESTIONS_REQUIRED, validQuestions.size())
            );
        }

       
        if (validQuestions.size() > MAX_QUESTIONS) {
            throw new IllegalArgumentException("Maximum " + MAX_QUESTIONS + " questions allowed");
        }

       
        List<Question> selectedQuestions = randomSelectQuestions(validQuestions, QUESTIONS_TO_SELECT);

       
        for (int i = 0; i < selectedQuestions.size(); i++) {
            selectedQuestions.get(i).setOrderIndex(i + 1);
        }

       
        questionRepository.deleteByAssignment_AssignmentId(assignmentId);

      
        questionRepository.saveAll(selectedQuestions);

    
        return ExcelUploadResponseDto.builder()
                .successCount(selectedQuestions.size())
                .errorCount(errors.size())
                .errors(errors.isEmpty() ? null : errors)
                .message(String.format(
                        "Successfully uploaded %d questions (randomly selected from %d valid questions). %d errors found in file.", 
                        selectedQuestions.size(), validQuestions.size(), errors.size()))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionDtoRes> getQuestionsByAssignmentId(Long assignmentId) {
        
        if (!assignmentRepository.existsById(assignmentId)) {
            throw new NotFoundException("Assignment not found with id: " + assignmentId);
        }


        List<Question> questions = questionRepository.findByAssignment_AssignmentIdOrderByOrderIndexAsc(assignmentId);


        return questions.stream()
                .map(question -> QuestionDtoRes.builder()
                        .questionId(question.getQuestionId())
                        .assignmentId(question.getAssignment().getAssignmentId())
                        .questionText(question.getQuestionText())
                        .optionA(question.getOptionA())
                        .optionB(question.getOptionB())
                        .optionC(question.getOptionC())
                        .optionD(question.getOptionD())
                        .correctAnswer(question.getCorrectAnswer())
                        .orderIndex(question.getOrderIndex())
                        .points(question.getPoints())
                        .build())
                .collect(Collectors.toList());
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or null");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
            throw new IllegalArgumentException("File must be Excel format (.xlsx or .xls)");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum limit of 5MB");
        }
    }

    private List<QuestionDtoReq> parseExcelFile(MultipartFile file) {
        List<QuestionDtoReq> questions = new ArrayList<>();

        String filename = file.getOriginalFilename();
        Workbook workbook;
        
        try {
            if (filename != null && filename.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(file.getInputStream());
            } else if (filename != null && filename.endsWith(".xls")) {
                workbook = new HSSFWorkbook(file.getInputStream());
            } else {
                throw new IllegalArgumentException("Unsupported file format. Only .xlsx and .xls are supported.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading Excel file: " + e.getMessage(), e);
        }

        try (workbook) {
            Sheet sheet = workbook.getSheetAt(0); 

            if (sheet == null) {
                throw new IllegalArgumentException("Excel file is empty");
            }

            
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                
                if (row == null) {
                    continue; 
                }

               
                if (isRowEmpty(row)) {
                    continue;
                }

                // Read 6 columns
                String questionText = getCellValueAsString(row.getCell(0));
                String optionA = getCellValueAsString(row.getCell(1));
                String optionB = getCellValueAsString(row.getCell(2));
                String optionC = getCellValueAsString(row.getCell(3));
                String optionD = getCellValueAsString(row.getCell(4));
                String correctAnswer = getCellValueAsString(row.getCell(5));

                // Skip if question text is empty
                if (questionText == null || questionText.trim().isEmpty()) {
                    continue;
                }

                QuestionDtoReq question = QuestionDtoReq.builder()
                        .questionText(questionText.trim())
                        .optionA(optionA != null ? optionA.trim() : "")
                        .optionB(optionB != null ? optionB.trim() : "")
                        .optionC(optionC != null ? optionC.trim() : "")
                        .optionD(optionD != null ? optionD.trim() : "")
                        .correctAnswer(correctAnswer != null ? correctAnswer.trim() : "")
                        .orderIndex(rowIndex) 
                        .points(1) 
                        .build();

                questions.add(question);
            }

        } catch (IOException e) {
            throw new RuntimeException("Error reading Excel file: " + e.getMessage(), e);
        }

        return questions;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
            
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == (long) numericValue) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        for (int cellIndex = 0; cellIndex < 6; cellIndex++) {
            Cell cell = row.getCell(cellIndex);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = getCellValueAsString(cell);
                if (value != null && !value.trim().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void validateQuestionData(QuestionDtoReq questionDto) {
        if (questionDto.getQuestionText() == null || questionDto.getQuestionText().trim().isEmpty()) {
            throw new IllegalArgumentException("Question text cannot be empty");
        }

        if (questionDto.getOptionA() == null || questionDto.getOptionA().trim().isEmpty()) {
            throw new IllegalArgumentException("Option A cannot be empty");
        }

        if (questionDto.getOptionB() == null || questionDto.getOptionB().trim().isEmpty()) {
            throw new IllegalArgumentException("Option B cannot be empty");
        }

        if (questionDto.getOptionC() == null || questionDto.getOptionC().trim().isEmpty()) {
            throw new IllegalArgumentException("Option C cannot be empty");
        }

        if (questionDto.getOptionD() == null || questionDto.getOptionD().trim().isEmpty()) {
            throw new IllegalArgumentException("Option D cannot be empty");
        }

        if (questionDto.getCorrectAnswer() == null || questionDto.getCorrectAnswer().trim().isEmpty()) {
            throw new IllegalArgumentException("Correct answer cannot be empty");
        }

        String correctAnswer = questionDto.getCorrectAnswer().toUpperCase().trim();
        if (!correctAnswer.equals("A") && !correctAnswer.equals("B") 
                && !correctAnswer.equals("C") && !correctAnswer.equals("D")) {
            throw new IllegalArgumentException("Correct answer must be A, B, C, or D");
        }
    }


    private List<Question> randomSelectQuestions(List<Question> questions, int n) {
        if (questions.size() <= n) {
            return new ArrayList<>(questions);
        }

      
        List<Question> shuffled = new ArrayList<>(questions);

        Collections.shuffle(shuffled);
        
  
        return shuffled.subList(0, n);
    }
}
