var androidPlay = function(url, showAds, isLive, callback) {
    cordova.exec(callback, function(err) {}, "videoplugin", "play", [url,showAds,isLive]);
}

module.exports = androidPlay;