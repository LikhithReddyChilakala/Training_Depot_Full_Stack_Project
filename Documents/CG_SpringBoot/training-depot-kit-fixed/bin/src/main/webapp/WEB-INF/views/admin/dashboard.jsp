<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Control Terminal" />
<%@ include file="/WEB-INF/views/fragments/head.jspf" %>
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<main class="depot-shell page-shell section" data-grid-id="main">
  <div class="shell-full">
    <span class="eyebrow">INVENTORY CONTROL TERMINAL</span>
    <h1>Depot Overview</h1>

    <nav style="display:flex; gap: var(--space-3); margin-bottom: var(--space-6);">
      <a href="${ctx}/admin/products" class="btn btn--ghost">EQUIPMENT RECORDS</a>
      <a href="${ctx}/admin/orders" class="btn btn--ghost">ORDER LEDGER</a>
    </nav>

    <div class="record-grid record-grid--compact">
      <div class="stat-tile">
        <div class="stat-tile__label">Active Records</div>
        <div class="stat-tile__value">${activeCount}</div>
      </div>
      <div class="stat-tile">
        <div class="stat-tile__label">Low Stock</div>
        <div class="stat-tile__value">${lowStockCount}</div>
      </div>
      <div class="stat-tile">
        <div class="stat-tile__label">Out Of Stock</div>
        <div class="stat-tile__value">${outOfStockCount}</div>
      </div>
      <div class="stat-tile">
        <div class="stat-tile__label">Confirmed Orders</div>
        <div class="stat-tile__value">${confirmedCount}</div>
      </div>
      <div class="stat-tile">
        <div class="stat-tile__label">Revenue (Confirmed)</div>
        <div class="stat-tile__value">&#8377;${revenue}</div>
      </div>
    </div>

    <h2 style="margin-top: var(--space-7);">Recent Orders</h2>
    <div class="table-scroll">
      <table class="depot-table">
        <thead><tr><th>Order</th><th>Status</th><th>Total</th></tr></thead>
        <tbody>
          <c:forEach items="${recentOrders}" var="order">
            <tr>
              <td class="mono"><a href="${ctx}/orders/${order.id}">${order.orderNumber}</a></td>
              <td>
                <c:choose>
                  <c:when test="${order.status == 'CONFIRMED'}"><span class="tag tag--confirmed">CONFIRMED</span></c:when>
                  <c:when test="${order.status == 'CANCELLED'}"><span class="tag tag--cancelled">CANCELLED</span></c:when>
                  <c:otherwise><span class="tag tag--pending">PENDING</span></c:otherwise>
                </c:choose>
              </td>
              <td class="mono">&#8377;${order.totalAmount}</td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>
  </div>
</main>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
