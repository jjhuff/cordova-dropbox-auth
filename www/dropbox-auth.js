var pluginName = 'DropboxAuth',
    exec = require('cordova/exec');

function DropboxAuth() {
}


DropboxAuth.prototype.isAuthenticated = function(successCB, failCB) {
    exec(successCB, failCB, pluginName, 'isAuthenticated', []);
};

DropboxAuth.prototype.auth = function(successCB, failCB) {
    exec(successCB, failCB, pluginName, 'auth', []);
};

DropboxAuth.prototype.unlink = function(successCB, failCB) {
    exec(successCB, failCB, pluginName, 'unlink', []);
};

DropboxAuth.prototype.getToken = function(successCB, failCB) {
    exec(successCB, failCB, pluginName, 'getToken', []);
};

module.exports = new DropboxAuth();
