(function () {
  var MAX_COMPARE = 3;
  var bar = document.querySelector(".compare-bar");
  if (!bar) {
    return;
  }
  var countEl = bar.querySelector(".compare-bar__count");
  var link = bar.querySelector(".compare-bar__link");
  var checkboxes = document.querySelectorAll(".compare-check");
  var base = link ? link.getAttribute("data-base-href") : "";

  function refresh() {
    var checked = Array.prototype.filter.call(checkboxes, function (cb) {
      return cb.checked;
    });
    if (checked.length > MAX_COMPARE) {
      checked[checked.length - 1].checked = false;
      checked.pop();
    }
    if (checked.length >= 2) {
      bar.classList.add("is-visible");
      if (countEl) {
        countEl.textContent = checked.length + " selected";
      }
      if (link) {
        var ids = checked.map(function (cb) {
          return cb.value;
        }).join(",");
        link.setAttribute("href", base + "?ids=" + ids);
      }
    } else {
      bar.classList.remove("is-visible");
    }
  }

  checkboxes.forEach(function (cb) {
    cb.addEventListener("change", refresh);
  });
  refresh();
})();
