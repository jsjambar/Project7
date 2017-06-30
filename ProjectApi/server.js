let express = require('express');
let app = express();
let port = process.env.PORT || 3000;
let mongoose = require('mongoose');
let User = require('./app/models/user');
let RotterdamDex = require('./app/models/rotterdam_dex');
let bodyparser = require('body-parser');

mongoose.Promise = global.Promise;
mongoose.connect('mongodb://localhost:27017/project');

app.use(bodyparser.urlencoded({ extended: true }));
app.use(bodyparser.json());

let routes = require('./app/routes/routes');
routes(app);

app.listen(port);

console.log(`API listening on port ${port}`);