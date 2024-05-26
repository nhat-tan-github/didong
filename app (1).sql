-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: localhost
-- Thời gian đã tạo: Th5 26, 2024 lúc 01:09 AM
-- Phiên bản máy phục vụ: 8.0.31
-- Phiên bản PHP: 7.4.33

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Cơ sở dữ liệu: `app`
--

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `class`
--

CREATE TABLE `class` (
  `id` int NOT NULL,
  `title` varchar(250) COLLATE utf8mb4_general_ci NOT NULL,
  `admin` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `class`
--

INSERT INTO `class` (`id`, `title`, `admin`) VALUES
(1, 'math', 1),
(2, 'Vật Lý', 1),
(3, 'Hóa học', 3),
(4, 'Giải TÍch 1', 1),
(5, 'allahuakba', 2),
(10, 'Giáo dục trẻ trâu', 2),
(11, 'lớp hoá thầy mmb', 2),
(12, 'Lớp 1 ', 2),
(17, 'trung tâm td học', 2),
(18, 'trại 03', 2),
(19, 'lớp mới', 2),
(20, 'Lớp 12C4', 6);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `posts`
--

CREATE TABLE `posts` (
  `post_id` int NOT NULL,
  `class_id` int NOT NULL,
  `author_id` int NOT NULL,
  `post_name` varchar(250) COLLATE utf8mb4_general_ci NOT NULL,
  `post_content` varchar(500) COLLATE utf8mb4_general_ci NOT NULL,
  `link_drive` varchar(250) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `day_created` datetime NOT NULL,
  `day_end` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `posts`
--

INSERT INTO `posts` (`post_id`, `class_id`, `author_id`, `post_name`, `post_content`, `link_drive`, `day_created`, `day_end`) VALUES
(4, 1, 2, 'news', 'anhnhatdeptrai', NULL, '2024-05-06 10:13:19', NULL),
(5, 1, 1, 'bài mới', 'cả nhóm thử đầm tập thể sau 10 phút', NULL, '2024-05-23 15:08:41', '2024-05-24 23:59:59'),
(6, 1, 1, 'bài mới', 'cả nhóm thử đầm tập thể sau 10 phút', NULL, '2024-05-23 15:09:03', '2024-05-24 23:59:59'),
(7, 1, 1, 'bài mới', 'cả nhóm thử đầm tập thể sau 10 phút', NULL, '2024-05-23 15:10:27', '2024-05-24 23:59:59'),
(8, 1, 1, 'bài mới', 'cả nhóm thử đầm tập thể sau 10 phút', NULL, '2024-05-23 15:11:22', '2024-05-24 23:59:59'),
(9, 1, 1, 'bài mới', 'cả nhóm thử đầm tập thể sau 10 phút', NULL, '2024-05-23 15:13:28', '2024-05-24 23:59:59'),
(10, 20, 6, 'fix', 'lmao', 'test', '2024-05-23 16:22:05', '2024-05-27 00:00:00'),
(11, 20, 6, 'a', 'a', NULL, '2024-05-23 16:22:12', '2024-05-24 23:59:59'),
(12, 20, 6, 'a', 'a', NULL, '2024-05-23 16:26:16', '2024-05-24 23:59:59'),
(13, 20, 6, 'bài tập thực hành', 'tạo chức năng bài tập', NULL, '2024-05-23 16:33:16', '2024-05-24 23:59:59'),
(14, 20, 6, 'a', 'a', NULL, '2024-05-23 17:05:13', '2024-05-23 00:00:00'),
(15, 20, 6, 'new', 'vidu', 'https://drive.google.com/file/d/1I_rO2J2npr2GqAJkK2_Z_eE4WTObjtKX/view?usp=sharing', '2024-05-24 10:26:15', '2024-05-23 00:00:00'),
(16, 20, 6, 'a', 'a', 'a', '2024-05-24 10:31:10', '2024-05-26 00:00:00'),
(17, 20, 6, 'a', 'a', '', '2024-05-24 10:39:08', '2024-05-26 00:00:00');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `student`
--

CREATE TABLE `student` (
  `id` int NOT NULL,
  `class_id` int NOT NULL,
  `student_id` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `student`
--

INSERT INTO `student` (`id`, `class_id`, `student_id`) VALUES
(5, 1, 5),
(6, 3, 5),
(7, 2, 5),
(8, 3, 2),
(9, 2, 2),
(14, 4, 2),
(17, 1, 7),
(18, 1, 8),
(22, 1, 6),
(23, 20, 8);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `submissions`
--

CREATE TABLE `submissions` (
  `submission_id` int NOT NULL,
  `assignment_id` int NOT NULL,
  `student_id` int NOT NULL,
  `submission_content` text COLLATE utf8mb4_general_ci NOT NULL,
  `submission_date` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `users`
--

CREATE TABLE `users` (
  `id` int NOT NULL,
  `unique_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `username` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `encrypted_password` varchar(250) COLLATE utf8mb4_general_ci NOT NULL,
  `salt` varchar(20) COLLATE utf8mb4_general_ci NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `users`
--

INSERT INTO `users` (`id`, `unique_id`, `username`, `email`, `encrypted_password`, `salt`, `created_at`, `updated_at`) VALUES
(1, 'b568fb91-0988-4da0-9a0e-f96fb3ac4b50', 'asd', 'a', '60dc25b9b1670011bdeed278cc9a03c6445e26ff35f625c1b8bbd9aea12eb2c7e6919b603a6d9e5b9dd80b394733c3aa783b6d298fa8c92c1609408ca5435d72', '431f63e3978d5fee', '2024-04-30 08:40:21', '2024-04-30 08:40:21'),
(2, '8cf28b59-2dc3-4f90-93c8-4fc2ca7a4d6b', 'asd', 'aa', 'f6576d78ddc19ddec66fd2c4801d4e3e15b22b03a907b6059ed9cf676a38060c20fd8a98e43c5a36923006ab0f34bd1409911eec870598f8179f4ccc358af9f5', 'dbdf42b46055a04a', '2024-05-01 09:17:09', '2024-05-22 14:29:25'),
(3, '59c07bd1-089c-4469-a1a4-df1d9d61a620', 'anhnhatdeptrai', '1onnisanvs4imouto', '5617f9a138da3a8dd315c95e01371e782f68b59cef10e777d119895e7f3f0922dddc8086c60f8a91049721c771d7c003d77248329284ce02d95209a083980299', '975811cf6ea476c1', '2024-05-01 14:10:18', '2024-05-01 14:10:18'),
(4, 'a3de3fb8-56f4-41b8-818e-9e3a9cabfdc9', 'topdog', 'test', '07839c51dff51cbf2b9384ea86e0e38d6fd928fbd65ec9dd928bb946568ca9c373aef08c0a5214096831734319b75f15208dcc0acb0df9df52ef20ee45bbee56', 'f71f60b1aa14e0eb', '2024-05-02 14:13:58', '2024-05-02 14:13:58'),
(5, '7cb93030-45bf-453b-9bb6-47713a7b2408', 'aa', 'aaa', '994cca4d47ceefe3e2d58e7a08638049cc8e098ffcbc147deb3d60f52259a7ce9a364f12afc52888a791a81628e4bad52437c3c0d647e1aedd3820cb78fc181f', '921f87c651237a24', '2024-05-12 10:17:35', '2024-05-12 10:17:35'),
(6, 'b3e43ab8-4f97-4ef8-b2f7-69d2d22a251b', 'nhat', 'vohuuhoangnhat5@gmail.com', '842419670ed6a9fcf88c1cfc89d1759d85b877ffa11e84f2a5e8aa6119c91266f057323393d81eb6ab5809fb52240e1df5b9abbe37e10c56c0f72d48a9e091b7', '55d6b253f62cafae', '2024-05-19 08:09:47', '2024-05-23 09:10:30'),
(7, 'ac92e81f-bc27-4487-94be-38f7f84ddc4b', '', '', '4951d9e9d92c9e5488720b785056b5b670ac53b8b15fae5ca5bfa1889b51d1a7b228d1f37107ab74f7c4908830673d55e0bc920fe16668fcb3900489d3096c37', '974a6850770d8844', '2024-05-22 09:40:20', '2024-05-22 09:40:20'),
(8, 'eecfb2f7-656a-4a80-8dc9-b62c3839a245', 'abc', 'vonhat10a4nh1@gmail.com', 'da8157495c80c0f0dcff5ca99cb8de2b41eae5f21b8171fb48fa552ae8e31fb733abc4570721da784082cc22c77e2fa217a8944c5f5cc06ad3257570895f2a39', '464149877880cf03', '2024-05-22 09:50:03', '2024-05-22 09:50:03'),
(9, 'f78c19e5-6d45-4caa-a71e-df83cc92dda2', 'siêu cấp phản đ', 'sjjxsbsjxh@gmail.com', '4d4312740a2703c9ebb2100219f03d048dd238892b9e46617bc5b5544c0e687d87ea6351c0a00cd952f540dfa05153a1973cdc240339e9832c3d215cc8b32a95', '2ad212b5aabadab1', '2024-05-22 09:55:32', '2024-05-22 09:55:32');

--
-- Chỉ mục cho các bảng đã đổ
--

--
-- Chỉ mục cho bảng `class`
--
ALTER TABLE `class`
  ADD PRIMARY KEY (`id`),
  ADD KEY `admin` (`admin`);

--
-- Chỉ mục cho bảng `posts`
--
ALTER TABLE `posts`
  ADD PRIMARY KEY (`post_id`),
  ADD KEY `class_id` (`class_id`),
  ADD KEY `author_id` (`author_id`),
  ADD KEY `post_name` (`post_name`);

--
-- Chỉ mục cho bảng `student`
--
ALTER TABLE `student`
  ADD PRIMARY KEY (`id`),
  ADD KEY `class_id` (`class_id`),
  ADD KEY `student_id` (`student_id`);

--
-- Chỉ mục cho bảng `submissions`
--
ALTER TABLE `submissions`
  ADD PRIMARY KEY (`submission_id`),
  ADD KEY `student_id` (`student_id`),
  ADD KEY `assignment_id` (`assignment_id`);

--
-- Chỉ mục cho bảng `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_id` (`unique_id`);

--
-- AUTO_INCREMENT cho các bảng đã đổ
--

--
-- AUTO_INCREMENT cho bảng `class`
--
ALTER TABLE `class`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT cho bảng `posts`
--
ALTER TABLE `posts`
  MODIFY `post_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT cho bảng `student`
--
ALTER TABLE `student`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- AUTO_INCREMENT cho bảng `submissions`
--
ALTER TABLE `submissions`
  MODIFY `submission_id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT cho bảng `users`
--
ALTER TABLE `users`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- Các ràng buộc cho các bảng đã đổ
--

--
-- Các ràng buộc cho bảng `class`
--
ALTER TABLE `class`
  ADD CONSTRAINT `class_ibfk_1` FOREIGN KEY (`admin`) REFERENCES `users` (`id`);

--
-- Các ràng buộc cho bảng `posts`
--
ALTER TABLE `posts`
  ADD CONSTRAINT `posts_ibfk_1` FOREIGN KEY (`class_id`) REFERENCES `class` (`id`),
  ADD CONSTRAINT `posts_ibfk_2` FOREIGN KEY (`author_id`) REFERENCES `users` (`id`);

--
-- Các ràng buộc cho bảng `student`
--
ALTER TABLE `student`
  ADD CONSTRAINT `student_ibfk_1` FOREIGN KEY (`class_id`) REFERENCES `class` (`id`),
  ADD CONSTRAINT `student_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`);

--
-- Các ràng buộc cho bảng `submissions`
--
ALTER TABLE `submissions`
  ADD CONSTRAINT `submissions_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `submissions_ibfk_2` FOREIGN KEY (`assignment_id`) REFERENCES `posts` (`post_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
