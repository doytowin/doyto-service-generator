+function ($) {
    'use strict';
    //var adminDirectives = angular.module('adminDirectives', []);
    //var modalModule = angular.module('angular-bootstrap.component.modal', [])

    adminDirectives.directive('modal', [
        '$rootElement',
        function ($rootElement) {
            return {
                restrict: 'C',
                link: function (scope, elem) {


                    var data// = elem.data('bs.modal')
                    var options = angular.extend({}, Modal.DEFAULTS, elem.data());
                    elem.data('bs.modal', (data = new Modal(elem[0], options)))
                    //if (typeof option == 'string') data[option](_relatedTarget)
                    //else if (options.show) data.show(_relatedTarget)
                    /*if (elem.hasClass('in')) {
                     $rootElement.addClass('has-modal');
                     if (elem.hasClass('modal-overlay')) {
                     $rootElement.addClass('has-modal-overlay');
                     elem.on('$destroy', function () {
                     $rootElement.removeClass('has-modal-overlay');
                     });
                     scope.$on('$destroy', function () {
                     $rootElement.removeClass('has-modal-overlay');
                     });
                     }
                     }
                     elem.on('$destroy', function () {
                     $rootElement.removeClass('has-modal');
                     });
                     scope.$on('$destroy', function () {
                     $rootElement.removeClass('has-modal');
                     });*/
                }
            };
        }]);
    adminDirectives.directive('toggle', [
        '$rootElement',
        function ($rootElement) {
            return {
                link: function ($scope, $elem, $attrs) {
                    $elem.on('click', function () {
                        var a = angular.element(document.querySelector($attrs.target));
                        var data = a.data('bs.' + $attrs.toggle);
                        data && data.toggle();
                    })

                }
            };
        }
    ])


    adminDirectives.directive('dismiss', [function () {
        return {
            link: function ($scope, $elem, $attrs) {
                $elem.on('click', function () {
                    angular.forEach(angular.element(document.getElementsByClassName($attrs.dismiss)), function (elem) {
                        var dismiss = angular.element(elem).data('bs.' + $attrs.dismiss);
                        dismiss && dismiss.hide();
                    })
                });

            }
        };
    }]);
//}());
//+function ($) {
//  'use strict';
    //var $ =  angular;

    // MODAL CLASS DEFINITION
    // ======================

    var Modal = function (element, options) {
        this.options = options
        this.$body = angular.element(document.body)
        this.$element = angular.element(element)
        this.$dialog = angular.element(element.querySelector('.modal-dialog'))
        this.$backdrop = null
        this.isShown = null
        this.originalBodyPad = null
        this.scrollbarWidth = 0
        this.ignoreBackdropClick = false

        /*if (this.options.remote) {
            this.$element
                .find('.modal-content')
                .load(this.options.remote, $.proxy(function () {
                    this.$element.trigger('loaded.bs.modal')
                }, this))
        }*/
    }

    Modal.VERSION = '3.3.6'

    Modal.TRANSITION_DURATION = 300
    Modal.BACKDROP_TRANSITION_DURATION = 150

    Modal.DEFAULTS = {
        backdrop: true,
        keyboard: true,
        show: true
    }

    Modal.prototype.toggle = function (_relatedTarget) {
        return this.isShown ? this.hide() : this.show(_relatedTarget)
    }

    Modal.prototype.show = function (_relatedTarget) {
        var that = this
        //var e = $.Event('show.bs.modal', {relatedTarget: _relatedTarget})

        //this.$element.trigger(e)

        if (this.isShown /*|| e.isDefaultPrevented()*/) return

        this.isShown = true

        this.checkScrollbar()
        this.setScrollbar()
        this.$body.addClass('modal-open')

        this.escape()
        this.resize()


        this.$dialog.on('mousedown', function () {
            that.$element.one('mouseup', function (e) {
                if (angular.equals(e.target, that.$element[0]))
                    that.ignoreBackdropClick = true
            })
        })

        this.backdrop(function () {
            var transition = /*$.support.transition &&*/ that.$element.hasClass('fade')

            if (!that.$element.parent().length) {
                that.$body.append(that.$element) // don't move modals dom position
            }

            that.$element
                .css('display', 'block')
                //.scrollTop(0)
            window.scrollTo(0,0);

            that.adjustDialog()

            if (transition) {
                that.$element[0].offsetWidth // force reflow
            }

            that.$element.addClass('in')

            that.enforceFocus()

            //var e = $.Event('shown.bs.modal', {relatedTarget: _relatedTarget})
            //
            //transition ?
            //    that.$dialog // wait for modal to slide in
                    //.one('bsTransitionEnd', function () {
                    //    that.$element.trigger('focus').trigger(e)
                    //})
                    //.emulateTransitionEnd(Modal.TRANSITION_DURATION) :
            //    that.$element.trigger('focus').trigger(e)
            that.$element.triggerHandler('focus')//.trigger(e)

        })
    }

    Modal.prototype.hide = function (e) {
        if (e) e.preventDefault()

        //e = $.Event('hide.bs.modal')
        //this.$element.trigger(e)

        if (!this.isShown/* || e.isDefaultPrevented()*/) return

        this.isShown = false

        this.escape()
        this.resize()

        angular.element(document).off('focusin')

        this.$element
            .removeClass('in')
            .off('click')
            .off('mouseup')

        this.$dialog.off('mousedown')

        //$.support.transition && this.$element.hasClass('fade') ?
        //    this.$element
                //.one('bsTransitionEnd', $.proxy(this.hideModal, this))
                //.emulateTransitionEnd(Modal.TRANSITION_DURATION) :
        //    this.hideModal()
        setTimeout(angular.bind(this, this.hideModal), Modal.BACKDROP_TRANSITION_DURATION);

    }

    Modal.prototype.enforceFocus = function () {
        angular.element(document)
            .off('focusin') // guard against infinite focus loop
            .on('focusin', angular.bind(this, function (e) {
                if (this.$element[0] !== e.target/* && !this.$element.has(e.target).length*/) {
                    this.$element.trigger('focus')
                }
            }))
    }

    Modal.prototype.escape = function () {
        if (this.isShown && this.options.keyboard) {
            this.$element.on('keydown', angular.bind(this, function (e) {
                e.which == 27 && this.hide()
            }))
        } else if (!this.isShown) {
            this.$element.off('keydown')
        }
    };

    Modal.prototype.resize = function () {
        if (this.isShown) {
            //$(window).on('resize.bs.modal', $.proxy(this.handleUpdate, this))
        } else {
            //$(window).off('resize.bs.modal')
        }
    }

    Modal.prototype.hideModal = function () {
        var that = this
        this.$element.css('display', 'none');
        this.backdrop(function () {
            that.$body.removeClass('modal-open')
            that.resetAdjustments()
            that.resetScrollbar()
        })
    }

    Modal.prototype.removeBackdrop = function () {
        this.$backdrop && this.$backdrop.remove();
        this.$backdrop = null;
    };

    Modal.prototype.backdrop = function (callback) {
        var that = this
        var animate = this.$element.hasClass('fade') ? 'fade' : ''

        if (this.isShown && this.options.backdrop) {
            var doAnimate = /*$.support.transition&&*/ animate

            this.$backdrop = angular.element(document.createElement('div'))
                .addClass('modal-backdrop ' + animate)
            this.$body.append(this.$backdrop)

            this.$element.on('click', angular.bind(this, function (e) {
                if (this.ignoreBackdropClick) {
                    this.ignoreBackdropClick = false;
                    return
                }
                if (e.target !== e.currentTarget) return;
                this.options.backdrop == 'static'
                    ? this.$element[0].focus()
                    : this.hide()
            }));

            if (doAnimate) this.$backdrop[0].offsetWidth // force reflow

            this.$backdrop.addClass('in')

            if (!callback) return

            //doAnimate ?
            //    this.$backdrop
            //        .one('bsTransitionEnd', callback)
            //        .emulateTransitionEnd(Modal.BACKDROP_TRANSITION_DURATION) :
            //    callback()
            setTimeout(callback, Modal.BACKDROP_TRANSITION_DURATION);


        } else if (!this.isShown && this.$backdrop) {
            this.$backdrop.removeClass('in')

            var callbackRemove = function () {
                that.removeBackdrop()
                callback && callback()
            }
            //$.support.transition && this.$element.hasClass('fade') ?
            //    this.$backdrop
            //        .one('bsTransitionEnd', callbackRemove)
            //        .emulateTransitionEnd(Modal.BACKDROP_TRANSITION_DURATION) :
            setTimeout(callbackRemove, Modal.BACKDROP_TRANSITION_DURATION);

            if (this.$element.hasClass('fade')) setTimeout(callbackRemove, Modal.BACKDROP_TRANSITION_DURATION);

        } else if (callback) {
            callback()
        }
    }

    // these following methods are used to handle overflowing modals

    Modal.prototype.handleUpdate = function () {
        this.adjustDialog()
    }

    Modal.prototype.adjustDialog = function () {
        var modalIsOverflowing = this.$element[0].scrollHeight > document.documentElement.clientHeight

        this.$element.css({
            paddingLeft:  !this.bodyIsOverflowing && modalIsOverflowing ? this.scrollbarWidth : '',
            paddingRight: this.bodyIsOverflowing && !modalIsOverflowing ? this.scrollbarWidth : ''
        })
    }

    Modal.prototype.resetAdjustments = function () {
        this.$element.css({
            paddingLeft: '',
            paddingRight: ''
        })
    }

    Modal.prototype.checkScrollbar = function () {
        var fullWindowWidth = window.innerWidth
        if (!fullWindowWidth) { // workaround for missing window.innerWidth in IE8
            var documentElementRect = document.documentElement.getBoundingClientRect()
            fullWindowWidth = documentElementRect.right - Math.abs(documentElementRect.left)
        }
        this.bodyIsOverflowing = document.body.clientWidth < fullWindowWidth
        this.scrollbarWidth = this.measureScrollbar()
    }

    Modal.prototype.setScrollbar = function () {
        var bodyPad = parseInt((this.$body.css('padding-right') || 0), 10)
        this.originalBodyPad = document.body.style.paddingRight || ''
        if (this.bodyIsOverflowing) this.$body.css('padding-right', bodyPad + this.scrollbarWidth)
    }

    Modal.prototype.resetScrollbar = function () {
        this.$body.css('padding-right', this.originalBodyPad)
    }

    Modal.prototype.measureScrollbar = function () { // thx walsh
        var scrollDiv = document.createElement('div')
        scrollDiv.className = 'modal-scrollbar-measure'
        this.$body.append(scrollDiv)
        var scrollbarWidth = scrollDiv.offsetWidth - scrollDiv.clientWidth
        this.$body[0].removeChild(scrollDiv)
        return scrollbarWidth
    }


}(angular);
