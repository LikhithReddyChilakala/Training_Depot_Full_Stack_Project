	<%@ taglib prefix="c" uri="jakarta.tags.core" %>
	<c:set var="pageTitle" value="Your Orders" />
	<%@ include file="/WEB-INF/views/fragments/head.jspf" %>
	<%@ include file="/WEB-INF/views/fragments/header.jspf" %>
	
	<main class="depot-shell page-shell section" data-grid-id="main">
	  <div class="shell-full">
	    <span class="eyebrow">ORDER LOG</span>
	    <h1>Your Orders</h1>
	
	    <c:choose>
	      <c:when test="${empty orders}">
	        <div class="empty-state">
	          <p>No orders yet.</p>
	          <a href="${ctx}/equipment" class="btn btn--primary">LOOK FOR EQUIPMENT</a>
	        </div>
	      </c:when>
	      <c:otherwise>
	        <div class="table-scroll">
	          <table class="depot-table">
	            <thead><tr><th>Order</th><th>Placed</th><th>Status</th><th>Total</th><th></th></tr></thead>
	            <tbody>
	              <c:forEach items="${orders}" var="order">
	                <tr>
	                  <td class="mono">${order.orderNumber}</td>
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
	      </c:otherwise>
	    </c:choose>
	  </div>
	</main>
	
	<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
