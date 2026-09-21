<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Register" />
<%@ include file="/WEB-INF/views/fragments/head.jspf" %>
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<main class="depot-shell page-shell section" data-grid-id="main">
  <div class="shell-full" style="max-width:440px; margin: 0 auto;">
    <span class="eyebrow">NEW MEMBER</span>
    <h1>Register For Depot Access</h1>

    <c:if test="${not empty error}"><div class="flash flash--error">${error}</div></c:if>

    <form method="post" action="${ctx}/register" class="depot-panel-card">
      <div class="depot-field">
        <label for="username">Username</label>
        <input type="text" id="username" name="username" value="${user.username}" required autofocus />
      </div>
      <div class="depot-field">
        <label for="email">Email</label>
        <input type="email" id="email" name="email" value="${user.email}" required />
      </div>
      <div class="depot-field">
        <label for="phoneNumber">Phone (optional)</label>
        <input type="tel" id="phoneNumber" name="phoneNumber" value="${user.phoneNumber}" />
      </div>
      <div class="depot-field">
        <label for="password">Password</label>
        <input type="password" id="password" name="password" required minlength="8" />
      </div>
      <button type="submit" class="btn btn--primary btn--block">CREATE DEPOT ACCESS</button>
    </form>

    <p style="margin-top: var(--space-4);">Already a member? <a href="${ctx}/login">Sign in</a>.</p>
  </div>
</main>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
