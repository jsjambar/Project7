let mongoose = require('mongoose');
let Schema = mongoose.Schema;

let userSchema = new Schema({
    username: {type: String, required: true, index: { unique: true}},
    password: {type: String, required: true}
});

module.exports = mongoose.model('User', userSchema);