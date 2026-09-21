(function () {
  function clamp(value, min, max) {
    if (value < min) return min;
    if (max != null && value > max) return max;
    return value;
  }

  document.querySelectorAll(".qty-stepper").forEach(function (stepper) {
    var input = stepper.querySelector("input[type=number]");
    var decrease = stepper.querySelector(".qty-decrease");
    var increase = stepper.querySelector(".qty-increase");
    if (!input) {
      return;
    }
    var min = parseInt(input.getAttribute("min") || "1", 10);
    var max = input.getAttribute("max") ? parseInt(input.getAttribute("max"), 10) : null;
    var autoSubmit = stepper.getAttribute("data-auto-submit") === "true";

    function apply(newValue) {
      input.value = clamp(newValue, min, max);
      if (autoSubmit) {
        var form = stepper.closest("form");
        if (form) {
          form.requestSubmit ? form.requestSubmit() : form.submit();
        }
      }
    }

    if (decrease) {
      decrease.addEventListener("click", function () {
        apply((parseInt(input.value, 10) || min) - 1);
      });
    }
    if (increase) {
      increase.addEventListener("click", function () {
        apply((parseInt(input.value, 10) || min) + 1);
      });
    }
    if (autoSubmit) {
      input.addEventListener("change", function () {
        apply(parseInt(input.value, 10) || min);
      });
    }
  });
})();
