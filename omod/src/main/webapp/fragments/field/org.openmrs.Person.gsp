${ ui.includeFragment("kenyaui", "widget/search", config.mergeAttributes([ searchType: "person" ])) }

<span id="${ config.id }-error" class="error" style="display: none"></span>
<% if (config.parentFormId) { %>
<script type="text/javascript">
	jq(function() {
		jq('#${ config.id }').data('default-value', jq('#${ config.id }').val());

		subscribe('${ config.parentFormId }.reset', function() {
			kenyaui.setSearchFieldValue('${ config.id }', jq('#${ config.id }').data('default-value'));
		});

		jq('#${ config.id }').on('change', function() {
			publish('${ config.parentFormId }/changed');
		});
	});
</script>
<% } %>