function generateRandom() {
      return Math.floor((1 + Math.random()) * 0x10000)
        .toString(16)
        .substring(1);
    }

    // This only works if `open` and `send` are called in a synchronous way
    // That is, after calling `open`, there must be no other call to `open` or
    // `send` from another place of the code until the matching `send` is called.
    requestID = null;
    XMLHttpRequest.prototype.reallyOpen = XMLHttpRequest.prototype.open;
    XMLHttpRequest.prototype.open = function(method, url, async, user, password) {
        requestID = generateRandom()
        var signed_url = url + "AJAXINTERCEPT" + requestID;
        this.reallyOpen(method, signed_url , async, user, password);
    };
    XMLHttpRequest.prototype.reallySend = XMLHttpRequest.prototype.send;
    XMLHttpRequest.prototype.send = function(body) {
        interception.customAjax(requestID, body);
        this.reallySend(body);
    };
    function onEditEventCallback(event) {
        var target = event.target;
        if (['input', 'textarea'].indexOf(target.localName) < 0) {
            return;
        }
        var id = target.id;
        if (id == null || id.trim().length <= 0) {
            target.id = id = generateRandom();
            var map = document.uiautoEditTextMap || {};
            map[id] = target;
            document.uiautoEditTextMap = map;
        }
        interception.onEditEvent(id, target.selectionStart, target.selectionEnd, target.value); 
    }
    document.addEventListener('onporpertychange', onEditEventCallback);
    document.addEventListener('change', onEditEventCallback);
    var ret = 'document.uiautoEditTextMap = ' + JSON.stringify(document.uiautoEditTextMap);
    ret