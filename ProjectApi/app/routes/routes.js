module.exports = (app) => {
  let user = require('../controllers/userController');

  app.route('/users').post(user.create);
};