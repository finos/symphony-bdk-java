<form id="${form_id}">
  <h3>Quote Registration</h3>
  <h6>From currency</h6>
  <text-field minlength="3" maxlength="3" masked="false" name="fromCurrency" required="true"></text-field>
  <h6>To currency</h6>
  <text-field minlength="3" maxlength="3" masked="false" name="toCurrency" required="true"></text-field>
  <h6>Amount</h6>
  <text-field minlength="1" maxlength="9" masked="false" name="amount" required="true"></text-field>
  <h6>Assigned To:</h6>
  <person-selector name="assignedTo" placeholder="Assign to.." required="false" />
  <h6>Quote Status:</h6>
  <radio name="status" checked="true" value="pending">Pending</radio>
  <radio name="status" checked="false" value="confirmed">Confirmed</radio>
  <radio name="status" checked="false" value="settled">Settled</radio>
  <h6>Remarks:</h6>
  <textarea name="remarks" placeholder="Enter your remarks.." required="false"></textarea>
  <button name="confirm" type="action">Confirm</button>
  <button name="reset" type="reset">Reset</button>
</form>