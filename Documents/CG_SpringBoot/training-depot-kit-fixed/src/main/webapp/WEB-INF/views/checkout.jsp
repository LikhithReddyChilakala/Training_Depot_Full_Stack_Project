<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Checkout" />
<%@ include file="/WEB-INF/views/fragments/head.jspf" %>
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<main class="depot-shell page-shell section" data-grid-id="main">
  <section class="shell-main--wide">
    <span class="eyebrow">REVIEW &amp; PAY</span>
    <h1>Checkout</h1>

    <c:if test="${not empty error}"><div class="flash flash--error">${error}</div></c:if>

    <div class="table-scroll">
      <table class="depot-table">
        <thead><tr><th>Record</th><th>Qty</th><th>Line Total</th></tr></thead>
        <tbody>
          <c:forEach items="${kit.lines}" var="line">
            <tr>
              <td>${line.product.name}</td>
              <td class="mono">${line.quantity}</td>
              <td class="mono">&#8377;${line.lineTotal}</td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>
  </section>

  <aside class="shell-aside">
    <div class="depot-panel-card">
      <h4 class="eyebrow">TOTAL DUE</h4>
      <p class="mono" style="font-size:24px; margin: 6px 0 var(--space-4);">&#8377;${kit.subtotal}</p>

      <c:choose>
        <c:when test="${not empty payment}">
          <button type="button" id="depot-pay-trigger" class="btn btn--primary btn--block">OPEN PAYMENT WINDOW</button>
          <p class="eyebrow" style="margin-top:var(--space-3);">If the payment window didn't open, use the button above.</p>
        </c:when>
        <c:otherwise>
          <form method="post" action="${ctx}/checkout/pay">
            <button type="submit" class="btn btn--primary btn--block">PAY &#8377;${kit.subtotal}</button>
          </form>
        </c:otherwise>
      </c:choose>

      <p style="margin-top: var(--space-3);"><a href="${ctx}/kit">&larr; BACK TO KIT</a></p>
    </div>
  </aside>
</main>

<c:if test="${not empty payment}">
<script src="https://checkout.razorpay.com/v1/checkout.js"></script>
<script>
  window.depotPayment = {
    keyId: "${payment.razorpayKeyId}",
    razorpayOrderId: "${payment.razorpayOrderId}",
    amountInPaise: ${payment.amountInPaise},
    internalOrderId: ${payment.internalOrderId},
    verifyUrl: "${ctx}/payment/verify"
  };
</script>
</c:if>
<script src="${ctx}/js/checkout.js"></script>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
