module.exports = (app) => {
  const user = require('../controllers/userController');
  const rotterdamDex = require('../controllers/rotterdamDexController');

  app.route('/users').post(user.create);
  app.route('/users/login').post(user.login);
  app.route('/rotterdam_dex/populate').post(rotterdamDex.populate);
};