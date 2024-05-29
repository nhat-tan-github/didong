var crypto = require ('crypto') ;
var uuid = require('uuid') ;
var express = require ('express') ;
var mysql = require ('mysql2') ;
var bodyParser = require ('body-parser') ;

//Connect to MySQL
var con = mysql. createConnection ({
host: 'localhost', // Replace your HOST IP
user: 'root',
password:'mysql',
database: 'app'
});


//PASSWORD ULTIL
var getRandomString = function (length) {
    return crypto.randomBytes (Math.ceil (length/2))
    .toString('hex') /* convert to hexa format */
    .slice(0, length); /* return required number of characters */
}
var sha512 = function (password, salt) {
    var hash = crypto.createHmac('sha512',salt); // Use SHA512
    hash.update (password) ;
    var value = hash.digest ('hex');
    return {
        salt:salt,
        passwordHash:value
    }
}
function saltHashPassword(userPassword) {
    var salt = getRandomString(16);
    var passwordData = sha512(userPassword,salt)
    return passwordData
}
var app=express () ;
app.use (bodyParser.json ()); // Accept JSON Params
app.use (bodyParser.urlencoded({extended: true})); // Accept URL Encoded params

function checkHashPassword (userPassword, salt){
    var passwordData = sha512(userPassword, salt) ;
    return passwordData;
}
app.post('/register/',(req,res,next)=>{
    var post_data = req.body

    var uid = uuid.v4 (); // Get UUID v4 like '110abacsasas-af0x-90333-casasjkajksk
    var plaint_password = post_data.password; // Get password from post params
    var hash_data = saltHashPassword(plaint_password) ;
    var password = hash_data.passwordHash; // Get hash value
    var salt = hash_data.salt; // Get salt

    var name = post_data.name;
    var email = post_data.email;

    con.query ('SELECT * FROM `users` where email =? ', [email], function (err, result, fields) {
        con.on ('error', function (err) {
        console.log ('[MySQL ERROR] ', err) ; 
    });
    if (result && result.length)
        res.json ('User already exists !!! ');
    else
    {
        con.query('INSERT INTO `users`( `unique_id`, `username`, `email`, `encrypted_password`, `salt`, `created_at`, `updated_at`) VALUES (?,?,?,?,?,NOW(),NOW())', [uid, name, email, password, salt], function(err, result, fields){
            con.on('error', function(err){
                console.log('[MySQL ERROR]', err);  
                res.json('Register error: ', err);
            });
            res.json('Register successful');
        });
        
    }
})
})
app.post('/login/', (req, res, next) => {
    var post_data = req.body;
    var user_password = post_data.password;
    var email = post_data.email;

    con.query('SELECT * FROM users WHERE email = ?', [email], function (err, result, fields) {
        if (err) {
            console.log('[MySQL ERROR] ', err);
            res.status(500).json('Internal server error');
            return;
        }

        if (result && result.length) {
            var salt = result[0].salt;
            var encrypted_password = result[0].encrypted_password;
            var hashed_password = checkHashPassword(user_password, salt).passwordHash;

            if (encrypted_password === hashed_password) {
                // Trả về thông tin người dùng dưới dạng đối tượng JSON
                res.json(result[0]);
            } else {
                res.status(401).json('Wrong password');
            }
        } else {
            res.status(404).json( {message:'User not exists !!!'});
        }
    });
});

app.post('/change-password', (req, res, next) => {
    var post_data = req.body;
    var email = post_data.email;
    var oldPassword = post_data.oldPassword;
    var newPassword = post_data.newPassword;

    con.query('SELECT * FROM users WHERE email = ?', [email], function(err, result, fields) {
        if (err) {
            console.log('[MySQL ERROR] ', err);
            res.status(500).json({ message:'Internal server error'});
            return;
        }

        if (result && result.length) {
            var salt = result[0].salt;
            var encrypted_password = result[0].encrypted_password;
            var hashed_old_password = checkHashPassword(oldPassword, salt).passwordHash;

            if (encrypted_password === hashed_old_password) {
                // Mã hóa mật khẩu mới
                var newHashedPasswordData = saltHashPassword(newPassword);
                var newEncryptedPassword = newHashedPasswordData.passwordHash;
                var newSalt = newHashedPasswordData.salt;

                // Cập nhật mật khẩu mới vào cơ sở dữ liệu
                con.query('UPDATE users SET encrypted_password = ?, salt = ?, updated_at = NOW() WHERE email = ?', 
                [newEncryptedPassword, newSalt, email], function(err, result, fields) {
                    if (err) {
                        console.log('[MySQL ERROR]', err);
                        res.status(500).json({ message:'Internal server error'});
                    } else {
                        res.json({ message:'Password updated successfully'});
                    }
                });
            } else {
                res.status(401).json({ message:'Wrong old password'});
            }
        } else {
            res.status(404).json({ message:'User not exists'});
        }
    });
});



async function queryDatabase(sql, params) {
    return new Promise((resolve, reject) => {
        con.query(sql, params, (err, result, fields) => {
            if (err) {
                reject(err);
            } else {
                resolve(result);
            }
        });
    });
}




//hiển thị lớp đã tham gia 
app.get('/class/user/:userId', async (req, res, next) => {
    try {
        var userId = req.params.userId;
        // Truy vấn các lớp mà người dùng đã tham gia
        const studentClasses = await queryDatabase('SELECT * FROM `student` WHERE student_id = ?', [userId]);
        if (studentClasses.length === 0) {
            return res.status(404).json('No classes found for this user');
        }
        // Thu thập các class IDs
        var classIds = studentClasses.map(row => row.class_id);
        // Truy vấn chi tiết các lớp dựa trên class IDs
        const classes = await queryDatabase('SELECT * FROM `class` WHERE id IN (?)', [classIds]);
        res.json(classes);
    } catch (err) {
        console.log('[MySQL ERROR]', err);
        res.status(500).json('Internal server error');
    }
});

// Hiển thị các lớp mà người dùng làm admin
app.get('/admin-classes/:adminId', async (req, res, next) => {
    try {
        var adminId = req.params.adminId;
        // Truy vấn các lớp mà người dùng là admin
        const adminClasses = await queryDatabase('SELECT * FROM `class` WHERE admin = ?', [adminId]);

        if (adminClasses.length === 0) {
            return res.status(404).json({ message: 'No classes found for this admin' });
        }

        res.json(adminClasses);
    } catch (err) {
        console.log('[MySQL ERROR]', err);
        res.status(500).json({ message: 'Internal server error' });
    }
});


//hiển thị bài trong 1 lớp 
app.get('/post/:classId', (req, res, next) => {
    var classId = req.params.classId;
    con.query('SELECT * FROM `posts` WHERE class_id = ?', [classId], function(err, result, fields) {
        if (err) {
            console.log('[MySQL ERROR]', err);
            res.status(500).json('Internal server error');
        } else {
            res.json(result);
        }
    });
});

//nộp bài
app.post('/submit', (req, res, next) => {
    var submissionData = req.body;
    var assignmentId = submissionData.assignment_id;
    var studentId = submissionData.student_id;
    var submissionDate = new Date();
    var submissionContent = submissionData.submission_content;

    con.query('INSERT INTO `submissions` (assignment_id, student_id, submission_date, submission_content) VALUES (?, ?, ?, ?)', [assignmentId, studentId, submissionDate, submissionContent], function(err, result, fields) {
        if (err) {
            console.log('[MySQL ERROR]', err);
            res.status(500).json('Internal server error');
        } else {
            res.json('Submission successful');
        }
    });
});

// Tạo bài tập (chỉ cho phép admin của lớp tạo bài tập)
app.post('/post', async (req, res, next) => {
    var postData = req.body;
    var classId = postData.class_id;
    var authorId = postData.author_id;
    var dayEnd = postData.day_end;
    var postName = postData.post_name;
    var postContent = postData.post_content;
    var linkDrive = postData.link_drive; // Thêm link_drive từ body request

    try {
        // Kiểm tra xem người dùng có phải là admin của lớp không
        const classResult = await queryDatabase('SELECT admin FROM class WHERE id = ?', [classId]);

        if (classResult.length === 0) {
            return res.status(404).json({ message: 'Class not found' });
        }

        var adminId = classResult[0].admin;

        if (adminId != authorId) {
            return res.status(403).json({ message: 'You are not authorized to create a post for this class' });
        }

        console.log('Attempting to create a post with classId:', classId, 'authorId:', authorId);
        console.log('Admin ID for classId', classId, 'is', adminId);

        // Nếu người dùng là admin, tiếp tục tạo bài
        con.query('INSERT INTO `posts` (class_id, author_id, post_name, post_content, day_created, day_end, link_drive) VALUES (?, ?, ?, ?, NOW(), ?, ?)', 
                  [classId, authorId, postName, postContent, dayEnd, linkDrive], 
                  function (err, result, fields) {
            if (err) {
                console.log('[MySQL ERROR]', err);
                res.status(500).json({ message:'Internal server error'});
            } else {
                res.json({ message:'Post created'});
            }
        });
    } catch (err) {
        console.log('[MySQL ERROR]', err);
        res.status(500).json({ message:'Internal server error'});
    }
});

// Tạo bài tập (chỉ cho phép admin của lớp tạo bài tập)
app.post('/post', async (req, res, next) => {
    var postData = req.body;
    var classId = postData.class_id;
    var authorId = postData.author_id;
    var dayEnd = postData.day_end;
    var postName = postData.post_name;
    var postContent = postData.post_content;
    var linkDrive = postData.link_drive || ''; 

    try {
        // Kiểm tra xem người dùng có phải là admin của lớp không
        const classResult = await queryDatabase('SELECT admin FROM class WHERE id = ?', [classId]);

        if (classResult.length === 0) {
            return res.status(404).json({ message: 'Class not found' });
        }

        var adminId = classResult[0].admin;

        if (adminId != authorId) {
            return res.status(403).json({ message: 'You are not authorized to create a post for this class' });
        }

        console.log('Attempting to create a post with classId:', classId, 'authorId:', authorId);
        console.log('Admin ID for classId', classId, 'is', adminId);

        // Nếu người dùng là admin, tiếp tục tạo bài
        con.query('INSERT INTO `posts` (class_id, author_id, post_name, post_content, day_created, day_end, link_drive) VALUES (?, ?, ?, ?, NOW(), ?, ?)', 
                  [classId, authorId, postName, postContent, dayEnd, linkDrive], 
                  function (err, result, fields) {
            if (err) {
                console.log('[MySQL ERROR]', err);
                res.status(500).json({ message:'Internal server error'});
            } else {
                res.json({ message:'Post created'});
            }
        });
    } catch (err) {
        console.log('[MySQL ERROR]', err);
        res.status(500).json({ message:'Internal server error'});
    }
});





//tạo lớp mới
app.post('/create-class/', (req, res, next) => {
    const { title, adminId } = req.body;
    var author_name = req.body.admin_name;

    console.log('Creating class with title:', title, 'and adminId:', adminId);

    con.query('INSERT INTO `class`(`title`, `admin`,`admin_name`) VALUES (?, ?,?)', [title, adminId,author_name], (err, result) => {
        if (err) {
            console.error('[MySQL ERROR]', err);
            return res.status(500).json({ error: 'Create class error', details: err });
        }

        console.log('Class created successfully:', result);
        res.json({ message: 'Class created successfully', result });
    });
});

// Tham gia lớp bằng ID lớp
app.post('/join-class/:classId', (req, res, next) => {
    var classId = req.params.classId;
    var studentId = req.body.studentId; // Nhận ID của học sinh từ body request

    // Kiểm tra xem học sinh đã tham gia lớp chưa
    con.query('SELECT * FROM `student` WHERE student_id = ? AND class_id = ?', [studentId, classId], function(err, result, fields) {
        if (err) {
            console.log('[MySQL ERROR]', err);
            res.status(500).json('Internal server error');
        } else {
            // Nếu học sinh đã tham gia lớp, không thực hiện thêm một lần nữa
            if (result && result.length > 0) {
                res.json('Đã tham gia lớp này!!!');
            } else {
                // Thêm học sinh vào lớp
                con.query('INSERT INTO `student` (class_id, student_id) VALUES (?, ?)', [classId, studentId], function(err, result, fields) {
                    if (err) {
                        console.log('[MySQL ERROR]', err);
                        res.status(500).json('Internal server error');
                    } else {
                        res.json('Tham gia thành công!');
                    }
                });
            }
        }
    });
});
// Thoát lớp bằng ID lớp
app.post('/leave-class/:classId', (req, res, next) => {
    var classId = req.params.classId;
    var studentId = req.body.studentId; // Nhận ID của học sinh từ body request

    // Kiểm tra xem học sinh có tham gia lớp không
    con.query('SELECT * FROM `student` WHERE student_id = ? AND class_id = ?', [studentId, classId], function(err, result, fields) {
        if (err) {
            console.log('[MySQL ERROR]', err);
            res.status(500).json('Internal server error');
        } else {
            // Nếu học sinh chưa tham gia lớp, không thực hiện gì thêm
            if (result && result.length === 0) {
                res.json('Chưa tham gia lớp này!!!');
            } else {
                // Xóa học sinh khỏi lớp
                con.query('DELETE FROM `student` WHERE student_id = ? AND class_id = ?', [studentId, classId], function(err, result, fields) {
                    if (err) {
                        console.log('[MySQL ERROR]', err);
                        res.status(500).json('Internal server error');
                    } else {
                        res.json('Rời khỏi lớp thành công!');
                    }
                });
            }
        }
    });
});
// Chỉnh sửa bài tập (chỉ cho phép admin của lớp chỉnh sửa bài tập)
app.put('/edit-post/:postId', async (req, res, next) => {
    try {
        var postId = req.params.postId;
        var userId = req.body.userId; 

        // Kiểm tra xem người gửi yêu cầu có phải là admin của lớp không
        const classResult = await queryDatabase('SELECT author_id FROM `posts` WHERE post_id = ?', [postId]);

        var adminId = classResult[0].author_id;

        if (adminId != userId) {
            return res.status(403).json({ message: 'You are not authorized to edit this post' });
        }

        // Nếu người gửi yêu cầu là admin, tiếp tục chỉnh sửa bài viết
        var newPostName = req.body.post_name;
        var newPostContent = req.body.post_content;
        var newLink_Drive = req.body.link_drive
        var newDay_End = req.body.day_end
        con.query('UPDATE `posts` SET post_name = ?, post_content = ?,link_drive=?,day_end=? WHERE post_id = ?', [newPostName, newPostContent,newLink_Drive,newDay_End, postId], function (err, result, fields) {
            if (err) {
                console.log('[MySQL ERROR]', err);
                res.status(500).json({ message:'Internal server error'});
            } else {
                res.json({ message:'Post edited'});
            }
        });
    } catch (err) {
        console.log('[MySQL ERROR]', err);
        res.status(500).json({ message:'Internal server error'});
    }
});

// Tìm bài đăng bằng post_id
app.get('/post-by-id/:postId', (req, res, next) => {
    var postId = req.params.postId;
    
    con.query('SELECT * FROM `posts` WHERE post_id = ?', [postId], function(err, result, fields) {
        if (err) {
            console.log('[MySQL ERROR]', err);
            res.status(500).json('Internal server error');
        } else if (result.length === 0) {
            res.status(404).json('Post not found');
        } else {
            res.json(result[0]);
        }
    });
});
// Xóa bài tập (chỉ cho phép admin của lớp xóa bài tập)
app.post('/delete-post/:postId', async (req, res, next) => {
    try {
        var postId = req.params.postId;
        var authorId = req.body.author_id; 

        // Kiểm tra xem người dùng có phải là admin của lớp chứa bài tập không
        const classResult = await queryDatabase('SELECT author_id FROM `posts` WHERE post_id = ?', [postId]);

        var adminId = classResult[0].author_id;

        if (adminId != authorId) {
            return res.status(403).json({ message: 'You are not authorized to delete this post' });
        }

        // Nếu người dùng là admin, tiếp tục xóa bài tập
        con.query('DELETE FROM `posts` WHERE post_id = ?', [postId], function (err, result, fields) {
            if (err) {
                console.log('[MySQL ERROR]', err);
                res.status(500).json({ message: 'Internal server error' });
            } else {
                res.json({ message: 'Post deleted' });
            }
        });
    } catch (err) {
        console.log('[MySQL ERROR]', err);
        res.status(500).json({ message: 'Internal server error' });
    }
});

//Start Server
app.listen(3000, ()=>{
    console.log ('Restful running on port 3000');
});