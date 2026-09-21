/*
 * Makes the structural grid feel like it reorganizes between pages instead
 * of a plain fade. Works across full page loads (this is a JSP app, not a
 * SPA) using sessionStorage as the handoff between "the page we're leaving"
 * and "the page we just arrived at".
 *
 * Elements that should participate carry data-grid-id="some-stable-name" -
 * see fragments/header.jspf, footer.jspf, and each view's main region.
 */
(function () {
  var STORAGE_KEY = "depotGridSnapshot";
  var ATTR = "data-grid-id";

  function captureSnapshot() {
    var nodes = document.querySelectorAll("[" + ATTR + "]");
    var snapshot = {};
    nodes.forEach(function (el) {
      var rect = el.getBoundingClientRect();
      snapshot[el.getAttribute(ATTR)] = { x: rect.left, y: rect.top };
    });
    try {
      sessionStorage.setItem(STORAGE_KEY, JSON.stringify(snapshot));
    } catch (e) {
      /* sessionStorage unavailable (private mode, quota) - navigation still works, just without the animation */
    }
  }

  function prefersReducedMotion() {
    return window.matchMedia && window.matchMedia("(prefers-reduced-motion: reduce)").matches;
  }

  function playTransitions() {
    var raw;
    try {
      raw = sessionStorage.getItem(STORAGE_KEY);
      sessionStorage.removeItem(STORAGE_KEY);
    } catch (e) {
      return;
    }
    if (!raw || prefersReducedMotion()) {
      return;
    }

    var previous;
    try {
      previous = JSON.parse(raw);
    } catch (e) {
      return;
    }

    document.querySelectorAll("[" + ATTR + "]").forEach(function (el) {
      var prevPos = previous[el.getAttribute(ATTR)];
      if (!prevPos) {
        // New structural block on this page - a quiet entrance rather than just appearing.
        el.classList.add("gt-enter");
        el.addEventListener("animationend", function done() {
          el.classList.remove("gt-enter");
          el.removeEventListener("animationend", done);
        });
        return;
      }

      var rect = el.getBoundingClientRect();
      var dx = prevPos.x - rect.left;
      var dy = prevPos.y - rect.top;
      if (Math.abs(dx) < 1 && Math.abs(dy) < 1) {
        return;
      }

      // FLIP: jump to the old position with no transition, then let the
      // browser animate it back to where it actually belongs.
      el.style.transform = "translate(" + dx + "px, " + dy + "px)";
      requestAnimationFrame(function () {
        requestAnimationFrame(function () {
          el.classList.add("gt-animating");
          el.style.transform = "";
          el.addEventListener("transitionend", function done() {
            el.classList.remove("gt-animating");
            el.style.willChange = "";
            el.removeEventListener("transitionend", done);
          });
        });
      });
    });
  }

  window.addEventListener("beforeunload", captureSnapshot);
  document.addEventListener("DOMContentLoaded", playTransitions);
})();
