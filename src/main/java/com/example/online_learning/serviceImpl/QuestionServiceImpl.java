package com.example.online_learning.serviceImpl;

import com.example.online_learning.constants.AssignmentType;
import com.example.online_learning.dto.request.QuestionDtoReq;
import com.example.online_learning.dto.request.WritingQuestionDtoReq;
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
    public List<QuestionDtoRes> getQuizQuestionsByAssignmentId(Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Assignment not found with id: " + assignmentId));

        if (assignment.getAssignmentType() != AssignmentType.QUIZ) {
            throw new IllegalArgumentException("This assignment is not a QUIZ type assignment");
        }

        List<Question> questions = questionRepository.findByAssignment_AssignmentIdOrderByOrderIndexAsc(assignmentId);
        List<QuestionDtoRes> result = new ArrayList<>();

        for (Question question : questions) {
            QuestionDtoRes dto = convertQuestionToDtoRes(question);
            result.add(dto);
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionDtoRes> getWritingQuestionsByAssignmentId(Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Assignment not found with id: " + assignmentId));

        if (assignment.getAssignmentType() != AssignmentType.WRITING) {
            throw new IllegalArgumentException("This assignment is not a WRITING type assignment");
        }

        List<Question> questions = questionRepository.findByAssignment_AssignmentIdOrderByOrderIndexAsc(assignmentId);
        List<QuestionDtoRes> result = new ArrayList<>();

        for (Question question : questions) {
            QuestionDtoRes dto = convertQuestionToDtoRes(question);
            result.add(dto);
        }

        return result;
    }

    @Override
    @Transactional
    public QuestionDtoRes createWritingQuestion(Long assignmentId, WritingQuestionDtoReq request) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Assignment not found with id: " + assignmentId));

        // Kiểm tra assignment phải là WRITING type
        if (assignment.getAssignmentType() != com.example.online_learning.constants.AssignmentType.WRITING) {
            throw new IllegalArgumentException("This assignment is not a WRITING type assignment");
        }

        // Lưu dữ liệu phức tạp vào questionData (JSON)
        String questionDataJson = null;
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            
            if (request.getQuestionType() == com.example.online_learning.constants.QuestionType.ESSAY_WRITING) {
                // Lưu thông tin về bài văn
                java.util.Map<String, Object> essayData = new java.util.HashMap<>();
                essayData.put("topic", request.getTopic());
                essayData.put("minWords", request.getMinWords());
                essayData.put("maxWords", request.getMaxWords());
                essayData.put("instructions", request.getInstructions());
                questionDataJson = mapper.writeValueAsString(essayData);
            } else if (request.getQuestionType() == com.example.online_learning.constants.QuestionType.REORDER) {
                // Lưu danh sách items
                if (request.getItems() != null && !request.getItems().isEmpty()) {
                    questionDataJson = mapper.writeValueAsString(request.getItems());
                }
            } else if (request.getQuestionType() == com.example.online_learning.constants.QuestionType.MATCHING) {
                // Lưu cột A và B
                java.util.Map<String, Object> matchingData = new java.util.HashMap<>();
                matchingData.put("columnA", request.getColumnA());
                matchingData.put("columnB", request.getColumnB());
                questionDataJson = mapper.writeValueAsString(matchingData);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error processing question data: " + e.getMessage(), e);
        }

        Question question = Question.builder()
                .assignment(assignment)
                .questionText(request.getQuestionText())
                .questionType(request.getQuestionType())
                .optionA("")
                .optionB("")
                .optionC("")
                .optionD("")
                .correctAnswer(request.getSampleAnswer())
                .orderIndex(request.getOrderIndex())
                .points(request.getPoints() != null ? request.getPoints() : 1)
                .questionData(questionDataJson)
                .build();

        question = questionRepository.save(question);
        return convertQuestionToDtoRes(question);
    }

    private QuestionDtoRes convertQuestionToDtoRes(Question question) {
        QuestionDtoRes.QuestionDtoResBuilder builder = QuestionDtoRes.builder()
                .questionId(question.getQuestionId())
                .assignmentId(question.getAssignment().getAssignmentId())
                .questionText(question.getQuestionText())
                .questionType(question.getQuestionType())
                .optionA(question.getOptionA())
                .optionB(question.getOptionB())
                .optionC(question.getOptionC())
                .optionD(question.getOptionD())
                .correctAnswer(question.getCorrectAnswer())
                .orderIndex(question.getOrderIndex())
                .points(question.getPoints());

        // Parse questionData nếu có
        if (question.getQuestionData() != null && !question.getQuestionData().isEmpty()) {
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                
                if (question.getQuestionType() == com.example.online_learning.constants.QuestionType.REORDER) {
                    // Parse items list
                    java.util.List<String> items = mapper.readValue(question.getQuestionData(), 
                            mapper.getTypeFactory().constructCollectionType(java.util.List.class, String.class));
                    builder.items(items);
                } else if (question.getQuestionType() == com.example.online_learning.constants.QuestionType.MATCHING) {
                    // Parse matching columns
                    com.fasterxml.jackson.databind.JsonNode jsonNode = mapper.readTree(question.getQuestionData());
                    if (jsonNode.has("columnA")) {
                        java.util.List<QuestionDtoRes.MatchingItemDto> columnA = 
                                mapper.convertValue(jsonNode.get("columnA"), 
                                        mapper.getTypeFactory().constructCollectionType(java.util.List.class, 
                                                QuestionDtoRes.MatchingItemDto.class));
                        builder.columnA(columnA);
                    }
                    if (jsonNode.has("columnB")) {
                        java.util.List<QuestionDtoRes.MatchingItemDto> columnB = 
                                mapper.convertValue(jsonNode.get("columnB"), 
                                        mapper.getTypeFactory().constructCollectionType(java.util.List.class, 
                                                QuestionDtoRes.MatchingItemDto.class));
                        builder.columnB(columnB);
                    }
                } else if (question.getQuestionType() == com.example.online_learning.constants.QuestionType.ESSAY_WRITING) {
                    // Parse essay data
                    com.fasterxml.jackson.databind.JsonNode jsonNode = mapper.readTree(question.getQuestionData());
                    if (jsonNode.has("topic")) {
                        builder.topic(jsonNode.get("topic").asText());
                    }
                    if (jsonNode.has("minWords")) {
                        builder.minWords(jsonNode.get("minWords").asInt());
                    }
                    if (jsonNode.has("maxWords")) {
                        builder.maxWords(jsonNode.get("maxWords").asInt());
                    }
                    if (jsonNode.has("instructions")) {
                        builder.instructions(jsonNode.get("instructions").asText());
                    }
                }
            } catch (Exception e) {
                // Nếu parse lỗi, chỉ log warning, không throw exception
                System.err.println("Warning: Could not parse questionData for question " + question.getQuestionId() + ": " + e.getMessage());
            }
        }

        return builder.build();
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
        Workbook workbook = null;
        
        try {
            if (filename != null && filename.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(file.getInputStream());
            } else if (filename != null && filename.endsWith(".xls")) {
                workbook = new HSSFWorkbook(file.getInputStream());
            } else {
                throw new IllegalArgumentException("Unsupported file format. Only .xlsx and .xls are supported.");
            }

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

                String questionText = getCellValueAsString(row.getCell(0));
                String optionA = getCellValueAsString(row.getCell(1));
                String optionB = getCellValueAsString(row.getCell(2));
                String optionC = getCellValueAsString(row.getCell(3));
                String optionD = getCellValueAsString(row.getCell(4));
                String correctAnswer = getCellValueAsString(row.getCell(5));

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
                        .points(5) 
                        .build();

                questions.add(question);
            }

        } catch (IOException e) {
            throw new RuntimeException("Error reading Excel file: " + e.getMessage(), e);
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException e) {
                    // Ignore close error
                }
            }
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
