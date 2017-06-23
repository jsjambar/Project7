let mongoose = require('mongoose');
User = mongoose.model('User');

// Create user
exports.create = ((request, result ) => {
    let user = new User({
        username: request.body.username,
        password: request.body.password
    });

    user.save((error, user) => {
        if(error) result.send(error);

        if(user.username === "" || user.password === "") return false;

        result.json(user);
    });
});

// Let user log in
exports.login = ((request, result) => {

    User.findOne({'username': request.body.username, password: request.body.password}, ((error, user) => {
        if(error) return false;

        result.json(user);
    }));
});