let mongoose = require('mongoose');
User = mongoose.model('User');

exports.create = ((request, result ) => {
    let user = new User({
        username: 'Jason',
        password: 'geheim'
    });

    user.save((error, user) => {
        if(error) result.send(error);

        result.json(user);
    });
});