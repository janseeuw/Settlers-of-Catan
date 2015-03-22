/**
 * Singleton used for Namespace
 */
function javaRest() {}

/**
 * GET method
 */
javaRest.get = function (url, data, success, error) {
  $.ajax({
    type: "GET",
    url: ''+url,
    contentType: "application/json",
    dataType: "json",
    async: false,
    crossDomain: true,
    data: JSON.stringify(data),
    success : success,
    error : error
  });
};

/**
 * POST method
 */
javaRest.post = function (url, data, success, error) {
  $.ajax({
    type: "POST",
    url: ''+url,
    contentType: "application/json",
    dataType: "json",
    async: false,
    crossDomain: true,
    data: JSON.stringify(data),
    success : success,
    error : error
  });
};

/*
 * Post with authentication
 */
javaRest.postAuth = function (url, data, success, error){
  $.ajax({
    type: "POST",
    url: ''+url,
    contentType: "application/json",
    dataType: "json",
    async: false,
    crossDomain: true,
    data: JSON.stringify(data),
    headers: {
      'Authorization' : 'basic ' + javaRest.cookie.get('credentials')
    },
    success : success,
    error : error
  });
};