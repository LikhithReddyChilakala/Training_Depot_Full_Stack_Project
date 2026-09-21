(function () {
  var list = document.getElementById("spec-rows");
  var addButton = document.getElementById("add-spec-row");

  function wireRemove(row) {
    var removeBtn = row.querySelector(".spec-row-remove");
    if (removeBtn) {
      removeBtn.addEventListener("click", function () {
        row.remove();
      });
    }
  }

  if (list) {
    list.querySelectorAll(".spec-row").forEach(wireRemove);
  }

  if (addButton && list) {
    addButton.addEventListener("click", function () {
      var row = document.createElement("div");
      row.className = "spec-row";
      row.innerHTML =
        '<input type="text" name="specKeys" placeholder="e.g. Material">' +
        '<input type="text" name="specValues" placeholder="e.g. Cast Iron">' +
        '<button type="button" class="spec-row-remove" aria-label="Remove row">&times;</button>';
      list.appendChild(row);
      wireRemove(row);
    });
  }

  var imageInput = document.getElementById("product-image-input");
  var preview = document.getElementById("product-image-preview");
  if (imageInput && preview) {
    imageInput.addEventListener("change", function () {
      var file = imageInput.files && imageInput.files[0];
      if (!file) {
        return;
      }
      preview.src = URL.createObjectURL(file);
      preview.style.display = "block";
    });
  }
})();
