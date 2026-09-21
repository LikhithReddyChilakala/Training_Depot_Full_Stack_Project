<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Order Ledger" />
<%@ include file="/WEB-INF/views/fragments/head.jspf" %>
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<main class="depot-shell page-shell section" data-grid-id="main">
  <div class="shell-full">
    <span class="eyebrow">INVENTORY CONTROL TERMINAL</span>
    <h1>Order Ledger</h1>
    <p class="eyebrow">Includes pending and cancelled checkout attempts, not just confirmed orders.</p>

    <div class="table-scroll">
      <table class="depot-table">
        <thead><tr><th>Order</th><th>Member</th><th>Placed</th><th>Status</th><th>Total</th><th></th></tr></thead>
        <tbody>
          <c:forEach items="${orders}" var="order">
            <tr>
              <td class="mono">${order.orderNumber}</td>
              <td>${order.user.username}</td>
              <td>${order.placedOnLabel}</td>
              <td>
                <c:choose>
                  <c:when test="${order.status == 'CONFIRMED'}"><span class="tag tag--confirmed">CONFIRMED</span></c:when>
                  <c:when test="${order.status == 'CANCELLED'}"><span class="tag tag--cancelled">CANCELLED</span></c:when>
                  <c:otherwise><span class="tag tag--pending">PENDING</span></c:otherwise>
                </c:choose>
              </td>
              <td class="mono">&#8377;${order.totalAmount}</td>
              <td><a href="${ctx}/orders/${order.id}" class="btn btn--ghost btn--small">VIEW</a></td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>
  </div>
</main>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
