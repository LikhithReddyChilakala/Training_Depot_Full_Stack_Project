<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="${isNew ? 'New' : 'Edit'} Equipment Record" />
<%@ include file="/WEB-INF/views/fragments/head.jspf" %>
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<main class="depot-shell page-shell section" data-grid-id="main">
  <div class="shell-full" style="max-width:640px;">
    <span class="eyebrow">INVENTORY CONTROL TERMINAL</span>
    <h1><c:choose><c:when test="${isNew}">New Equipment Record</c:when><c:otherwise>Edit ${product.recordNumber}</c:otherwise></c:choose></h1>

    <c:choose>
      <c:when test="${isNew}"><c:set var="formAction" value="${ctx}/admin/products" /></c:when>
      <c:otherwise><c:set var="formAction" value="${ctx}/admin/products/${product.id}" /></c:otherwise>
    </c:choose>

    <form method="post"
          action="${formAction}"
          enctype="multipart/form-data" class="depot-panel-card">

      <div class="depot-field">
        <label for="name">Name</label>
        <input type="text" id="name" name="name" value="${product.name}" required />
      </div>

      <div class="depot-field">
        <label for="description">Description</label>
        <textarea id="description" name="description" rows="3" required>${product.description}</textarea>
      </div>

      <div class="depot-field">
        <label for="category">Category</label>
        <select id="category" name="category" required>
          <c:forEach items="${categories}" var="cat">
            <option value="${cat}" ${product.category == cat ? 'selected' : ''}>${cat.label}</option>
          </c:forEach>
        </select>
      </div>

      <div style="display:grid; grid-template-columns:1fr 1fr 1fr; gap: var(--space-3);">
        <div class="depot-field">
          <label for="price">Price (&#8377;)</label>
          <input type="number" id="price" name="price" value="${product.price}" step="0.01" min="0" required />
        </div>
        <div class="depot-field">
          <label for="discountPercent">Discount %</label>
          <input type="number" id="discountPercent" name="discountPercent" value="${product.discountPercent}" min="0" max="90" required />
        </div>
        <div class="depot-field">
          <label for="stockQuantity">Stock</label>
          <input type="number" id="stockQuantity" name="stockQuantity" value="${product.stockQuantity}" min="0" required />
        </div>
      </div>

      <div class="depot-field">
        <label>Specifications</label>
        <div id="spec-rows">
          <c:forEach items="${product.specKeys}" varStatus="st">
            <div class="spec-row">
              <input type="text" name="specKeys" value="${product.specKeys[st.index]}" placeholder="e.g. Material" />
              <input type="text" name="specValues" value="${product.specValues[st.index]}" placeholder="e.g. Cast Iron" />
              <button type="button" class="spec-row-remove" aria-label="Remove row">&times;</button>
            </div>
          </c:forEach>
        </div>
        <button type="button" id="add-spec-row" class="btn btn--ghost btn--small" style="margin-top:6px;">+ ADD SPECIFICATION</button>
      </div>

      <div class="depot-field">
        <label for="product-image-input">Equipment Photo (optional)</label>
        <input type="file" id="product-image-input" name="image" accept=".jpg,.jpeg,.png,.webp" />
        <c:if test="${not empty product.imagePath}">
          <img src="${ctx}/uploads/images/${product.imagePath}" alt="" style="max-width:120px; margin-top:8px;" />
        </c:if>
        <img id="product-image-preview" alt="" style="max-width:120px; margin-top:8px; display:none;" />
      </div>

      <div class="depot-field depot-field--checkbox">
        <input type="hidden" name="_active" value="on" />
        <input type="checkbox" id="active" name="active" value="true" ${isNew || product.active ? 'checked' : ''} />
        <label for="active" style="margin:0;">Visible on the storefront</label>
      </div>

      <button type="submit" class="btn btn--primary btn--block">${isNew ? 'CREATE RECORD' : 'SAVE CHANGES'}</button>
    </form>

    <p style="margin-top: var(--space-4);"><a href="${ctx}/admin/products">&larr; BACK TO EQUIPMENT RECORDS</a></p>
  </div>
</main>

<script src="${ctx}/js/admin.js"></script>
<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
