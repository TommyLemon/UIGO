  var map = document.uiautoEditTextMap || {};
  var et = map[targetWebId] || document.getElementById(targetWebId);
  if (et == null) {
    et = map[x + ',' + y];
  }
  if (et == null) {
    function findEditText(x, y) {

      var inputs = document.getElementsByTagName('input');
      var textareas = document.getElementsByTagName('textarea');

      function getZIndex(e) {
        if (e instanceof HTMLElement == false) {
          return null;
        }

        var style = document.defaultView.getComputedStyle(e);
        var z = style == null ? null : style.getPropertyValue('z-index');
        return z == null || Number.isNaN(z) ? getZIndex(e.parentNode) : z;
      }

      function findItem(editTexts, target) {
        if (editTexts == null || editTexts.length <= 0) {
          return target;
        }

        var tz = getZIndex(target);
        for (var i = 0; i < editTexts.length; i ++) {
          var et = editTexts.item(i);

          var rect = et == null || et.disabled ? null : et.getBoundingClientRect();
          var left = rect == null ? null : rect.left;
          var right = left == null ? null : rect.right;
          var top = right == null ? null : rect.top;
          var bottom = top == null ? null : rect.bottom;
          if (bottom == null || x < left || x > right || y < top || y > bottom) {
            continue;
          }

          if (target == null) {
            target = et;
            continue;
          }

          var z = getZIndex(et);
          if (tz == null || (z != null && z > tz)) {
            target = et;
            tz = z;
          }
        }

        return target;
      }


      var target = findItem(inputs, null);
      var target2 = findItem(textareas, target);

      console.log("findViewByPoint(" + x + ", " + y + ") = " + (target2 == null ? null : target2.id));
      return target2;
    }

    et = findEditText(x, y);
    map[x + ',' + y] = et;
  }

  et.value = value;
  try {
    et.focus();
  } catch (e) {
    console.log(e);
  }
  et;