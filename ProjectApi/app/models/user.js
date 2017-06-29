let mongoose = require('mongoose');
let Schema = mongoose.Schema;

// Finish this
let userSchema = new Schema({
    username: {type: String, required: true, index: { unique: true}},
    password: {type: String, required: true},
    RotterdamDex: [{type: Schema.Types.ObjectId, ref: 'RotterdamDex'}]
});

module.exports = mongoose.model('User', userSchema);