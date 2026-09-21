<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Order ${order.orderNumber}" />
<%@ include file="/WEB-INF/views/fragments/head.jspf" %>
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<main class="depot-shell page-shell section" data-grid-id="main">
  <div class="shell-full" style="max-width:640px;">
    <span class="eyebrow">ORDER ${order.orderNumber}</span>

    <c:choose>
      <c:when test="${order.status == 'CONFIRMED'}">
        <h1>Order Confirmed</h1>
        <div class="flash flash--success">Your equipment has been reserved from the depot floor. A confirmation email is on its way.</div>
      </c:when>
      <c:when test="${order.status == 'CANCELLED'}">
        <h1>Order Not Completed</h1>
        <c:if test="${not empty error}"><div class="flash flash--error">${error}</div></c:if>
        <c:if test="${empty error}"><div class="flash flash--error">This order was cancelled. Your kit has not been charged for undelivered stock.</div></c:if>
        <p><a href="${ctx}/kit" class="btn btn--ghost">RETURN TO KIT</a></p>
      </c:when>
      <c:otherwise>
        <h1>Order Pending</h1>
        <div class="flash">Payment for this order hasn't been confirmed yet.</div>
      </c:otherwise>
    </c:choose>

    <table class="depot-table" style="margin-top: var(--space-4);">
      <thead><tr><th>Record</th><th>Qty</th><th>Unit</th><th>Line Total</th></tr></thead>
      <tbody>
        <c:forEach items="${order.orderItems}" var="item">
          <tr>
            <td>${item.productName}</td>
            <td class="mono">${item.quantity}</td>
            <td class="mono">&#8377;${item.unitPrice}</td>
            <td class="mono">&#8377;${item.lineTotal}</td>
          </tr>
        </c:forEach>
      </tbody>
    </table>

    <p class="mono" style="text-align:right; font-size:18px; margin-top:var(--space-3);">TOTAL &#8377;${order.totalAmount}</p>

    <p style="margin-top: var(--space-5);"><a href="${ctx}/orders">&larr; VIEW ALL ORDERS</a> &nbsp;&middot;&nbsp; <a href="${ctx}/equipment">LOOK FOR MORE EQUIPMENT</a></p>
  </div>
</main>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
