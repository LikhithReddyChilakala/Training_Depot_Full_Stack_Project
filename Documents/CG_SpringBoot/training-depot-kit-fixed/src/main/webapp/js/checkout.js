(function () {
  function postVerification(payment, response) {
    var form = document.createElement("form");
    form.method = "POST";
    form.action = payment.verifyUrl;
    var fields = {
      internalOrderId: payment.internalOrderId,
      razorpay_payment_id: response.razorpay_payment_id,
      razorpay_order_id: response.razorpay_order_id,
      razorpay_signature: response.razorpay_signature
    };
    Object.keys(fields).forEach(function (name) {
      var input = document.createElement("input");
      input.type = "hidden";
      input.name = name;
      input.value = fields[name];
      form.appendChild(input);
    });
    document.body.appendChild(form);
    form.submit();
  }

  function openRazorpay() {
    var payment = window.depotPayment;
    if (!payment || typeof Razorpay === "undefined") {
      return;
    }
    var instance = new Razorpay({
      key: payment.keyId,
      amount: payment.amountInPaise,
      currency: "INR",
      order_id: payment.razorpayOrderId,
      name: "Training Depot",
      description: "Equipment kit payment",
      theme: { color: "#AE4420" },
      handler: function (response) {
        postVerification(payment, response);
      }
    });
    instance.open();
  }

  document.addEventListener("DOMContentLoaded", function () {
    if (window.depotPayment) {
      openRazorpay();
    }
    var retryButton = document.getElementById("depot-pay-trigger");
    if (retryButton) {
      retryButton.addEventListener("click", openRazorpay);
    }
  });
})();
