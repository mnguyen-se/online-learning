-- Migration: Fix correct_answer column to support TEXT for writing assignments
-- Reason: Writing assignment sample answers can be long strings (e.g., "わたしは学生です", essay answers)
-- Date: 2026-02-15

-- Change correct_answer column from VARCHAR to TEXT to support longer answers
ALTER TABLE question MODIFY COLUMN correct_answer TEXT NULL;
