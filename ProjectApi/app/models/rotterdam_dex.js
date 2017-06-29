const mongoose = require('mongoose');
const Schema = mongoose.Schema;

let rotterdamDexSchema = Schema({
    name: {type: String, required: true, index: {unique: true}},
    lat: String,
    lon: String,
    description: String,
    finished: Boolean
});

module.exports = mongoose.model('RotterdamDex', rotterdamDexSchema);