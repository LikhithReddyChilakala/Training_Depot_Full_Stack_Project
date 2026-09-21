<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="depot" tagdir="/WEB-INF/tags" %>
<c:set var="pageTitle" value="Kit" />
<%@ include file="/WEB-INF/views/fragments/head.jspf" %>
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<main class="depot-shell page-shell section" data-grid-id="main">
  <section class="shell-main--wide">
    <span class="eyebrow">KIT SUMMARY</span>
    <h1>Your Kit</h1>

    <c:if test="${success != null}"><div class="flash flash--success">${success}</div></c:if>
    <c:if test="${error != null}"><div class="flash flash--error">${error}</div></c:if>

    <c:if test="${kit.hasIssues()}">
      <div class="flash flash--error">
        Some items in your kit changed since you added them - quantities below reflect what's actually available.
      </div>
    </c:if>

    <c:choose>
      <c:when test="${kit.lines.size() == 0}">
        <div class="empty-state">
          <p>Your kit is empty.</p>
          <a href="${ctx}/equipment" class="btn btn--primary">LOOK FOR EQUIPMENT</a>
        </div>
      </c:when>
      <c:otherwise>
        <div class="table-scroll">
          <table class="depot-table">
            <thead>
              <tr><th>Record</th><th>Unit</th><th>Qty</th><th>Line Total</th><th></th></tr>
            </thead>
            <tbody>
              <c:forEach items="${kit.lines}" var="line">
                <tr>
                  <td>
                    <a href="${ctx}/equipment/${line.product.id}">${line.product.name}</a>
                    <div class="eyebrow">${line.product.recordNumber}</div>
                  </td>
                  <td class="mono">&#8377;${line.unitPrice}</td>
                  <td>
                    <c:choose>
                      <c:when test="${line.quantity > 0}">
                        <form method="post" action="${ctx}/kit/update">
                          <input type="hidden" name="productId" value="${line.product.id}" />
                          <div class="qty-stepper" data-auto-submit="true">
                            <button type="button" class="qty-decrease" aria-label="Decrease quantity">&minus;</button>
                            <input type="number" name="quantity" value="${line.quantity}" min="1" max="${line.product.stockQuantity}" />
                            <button type="button" class="qty-increase" aria-label="Increase quantity">+</button>
                          </div>
                        </form>
                      </c:when>
                      <c:otherwise>
                        <span class="status-line"><span class="status-dot is-out"></span>OUT OF STOCK</span>
                      </c:otherwise>
                    </c:choose>
                  </td>
                  <td class="mono">&#8377;${line.lineTotal}</td>
                  <td>
                    <form method="post" action="${ctx}/kit/remove">
                      <input type="hidden" name="productId" value="${line.product.id}" />
                      <button type="submit" class="btn btn--ghost btn--small">REMOVE</button>
                    </form>
                  </td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
      </c:otherwise>
    </c:choose>
  </section>

  <aside class="shell-aside">
    <div class="depot-panel-card">
      <h4 class="eyebrow">SUBTOTAL</h4>
      <p class="mono" style="font-size:24px; margin: 6px 0 var(--space-4);">&#8377;${kit.subtotal}</p>
      <c:if test="${kit.lines.size() > 0}">
        <a href="${ctx}/checkout" class="btn btn--primary btn--block">PROCEED TO CHECKOUT</a>
      </c:if>
      <p style="margin-top: var(--space-3);"><a href="${ctx}/equipment">&larr; KEEP LOOKING FOR EQUIPMENT</a></p>
    </div>
  </aside>
</main>

<script src="${ctx}/js/kit.js"></script>
<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
