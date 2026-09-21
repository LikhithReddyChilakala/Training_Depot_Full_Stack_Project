<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageTitle" value="Order Details" />

<%@ include file="/WEB-INF/views/fragments/head.jspf" %>

<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<main class="depot-shell page-shell section" data-grid-id="main">

  <div class="shell-full">

    <span class="eyebrow">ORDER RECORD</span>

    <h1>${order.orderNumber}</h1>

    <div class="section-grid">

      <section class="shell-main--wide">

        <div class="depot-panel-card">

          <div class="record-header">

            <div>
              <span class="eyebrow">ORDER STATUS</span>

              <c:choose>
                <c:when test="${order.status == 'CONFIRMED'}">
                  <span class="tag tag--confirmed">CONFIRMED</span>
                </c:when>

                <c:when test="${order.status == 'CANCELLED'}">
                  <span class="tag tag--cancelled">CANCELLED</span>
                </c:when>

                <c:otherwise>
                  <span class="tag tag--pending">PENDING</span>
                </c:otherwise>
              </c:choose>
            </div>

            <div>
              <span class="eyebrow">PLACED</span>
              <p class="mono">${order.placedOnLabel}</p>
            </div>

          </div>

          <div class="table-scroll">

            <table class="depot-table">

              <thead>
                <tr>
                  <th>Equipment</th>
                  <th>Qty</th>
                  <th>Unit Price</th>
                  <th>Line Total</th>
                </tr>
              </thead>

              <tbody>

                <c:forEach items="${order.orderItems}" var="item">

                  <tr>

                    <td>
                      ${item.productName}
                    </td>

                    <td class="mono">
                      ${item.quantity}
                    </td>

                    <td class="mono">
                      &#8377;${item.unitPrice}
                    </td>

                    <td class="mono">
                      &#8377;${item.unitPrice * item.quantity}
                    </td>

                  </tr>

                </c:forEach>

              </tbody>

            </table>

          </div>

        </div>

      </section>

      <aside class="shell-aside">

        <div class="depot-panel-card">

          <span class="eyebrow">ORDER TOTAL</span>

          <p class="mono" style="font-size:28px; margin:6px 0 var(--space-4);">
            &#8377;${order.totalAmount}
          </p>

          <p>
            Thank you for training with us. Your order has been recorded successfully.
          </p>

          <p style="margin-top:var(--space-4);">
            <a href="${ctx}/equipment" class="btn btn--primary btn--block">
              LOOK FOR EQUIPMENT
            </a>
          </p>

          <p style="margin-top:var(--space-3);">
            <a href="${ctx}/orders" class="btn btn--ghost btn--block">
              BACK TO ORDER LOG
            </a>
          </p>

        </div>

      </aside>

    </div>

  </div>

</main>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>