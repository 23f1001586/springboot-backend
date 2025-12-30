-- Run this SQL script to alter the profile_pic column to support larger images
-- This fixes the "Data too long for column 'profile_pic'" error

USE zenbuy;

-- Alter the profile_pic column to LONGTEXT to support large base64 images
ALTER TABLE users MODIFY COLUMN profile_pic LONGTEXT;

