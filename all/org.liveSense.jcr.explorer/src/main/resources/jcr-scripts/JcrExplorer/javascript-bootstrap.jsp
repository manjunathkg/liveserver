<script src="bootstrap/js/bootstrap.js"></script>
<script src="bootstrap/js/bootstrap.js"></script>
<script src="bootstrap/js/bootstrap-transition.js"></script>
<script src="bootstrap/js/bootstrap-alert.js"></script>
<script src="bootstrap/js/bootstrap-modal.js"></script>
<script src="bootstrap/js/bootstrap-dropdown.js"></script>
<script src="bootstrap/js/bootstrap-scrollspy.js"></script>
<script src="bootstrap/js/bootstrap-tab.js"></script>
<script src="bootstrap/js/bootstrap-tooltip.js"></script>
<script src="bootstrap/js/bootstrap-popover.js"></script>
<script src="bootstrap/js/bootstrap-button.js"></script>
<script src="bootstrap/js/bootstrap-collapse.js"></script>
<script src="bootstrap/js/bootstrap-carousel.js"></script>
<script src="bootstrap/js/bootstrap-typeahead.js"></script>
<script src="bootstrap/js/bootstrap-affix.js"></script>
<script src="bootstrap/js/bootstrap-combobox.js"></script>

<script src="bootbox/js/bootbox.min.js"></script>

	<script>
	$(document).ready(function() {
				$("a[rel=popover]").popover().
				mouseenter(function(e){
					$(this).popover('show');
				}).mouseleave(function(e) {
					$(this).popover('hide');
				})

				$("input[rel=popover]").popover().
				mouseenter(function(e){
					$(this).popover('show');
				}).mouseleave(function(e) {
					$(this).popover('hide');
				})

				$('.combobox').combobox();
	});
	</script>
