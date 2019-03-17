/*!
 * restore$ref.js v1.0.0
 *
 * 还原fastjson的循环引用检测替换掉的$ref对象
 *
 */
var restore$ref = function () {
    function resolvePath(root, expression) {
        var refs = expression.split(/[\.\[\]]+/);
        var resolved = root;
        for (var i = 1, j = refs.length; i < j; ++i) {
            var ref = refs[i];
            if (ref !== '') {
                if (isNaN(ref)) {
                    resolved = resolved[ref];
                } else {
                    resolved = resolved[parseInt(ref)];
                }
            }
        }
        return resolved;
    }

    /**
     * {"$ref":"$"}         引用根对象
     * {"$ref":"@"}         引用自己
     * {"$ref":".."}        引用父对象
     * {"$ref":"../.."}     引用父对象的父对象
     * {"$ref":"$.members[0].reportTo"}    基于路径的引用
     */
    function resolve$ref(root, ancient, key) {
        var parent = ancient.node;
        var $ref = parent[key].$ref;
        if ($ref.substring(0, 1) == "$") {
            parent[key] = resolvePath(root, $ref);
        } else if ($ref == "@") {
            parent[key] = ancient.node;
        } else if ($ref.substring(0, 2) == "..") {
            var len = $ref.split(/[\/]+/).length;
            while (len-- > 0) {
                ancient = ancient.parent;
            }
            parent[key] = ancient.node;
        } else {
            console.log("无法识别的$ref:" + $ref);
        }
    }

    /**
     * 递归解析JSON
     *
     * @param root      原始的JSON对象
     * @param ancient   ancient.node为父节点, 用于索引祖先
     * @param key       ancient.node[key]是当前需要解析的节点
     */
    function resolveObject(root, ancient, key) {
        var current = ancient ? ancient.node[key] : root;
        if (current !== null && typeof current === "object") {
            if (Array.isArray(current)) {
                for (var i = 0; i < current.length; i++) {
                    resolveObject(root, {
                        parent: ancient,
                        node: current
                    }, i);
                }
            } else {
                if (current.$ref) {
                    resolve$ref(root, ancient, key);//处理$ref
                } else {
                    for (var p in current) {
                        if (current.hasOwnProperty(p)) {
                            resolveObject(root, {
                                parent: ancient,
                                node: current
                            }, p)
                        }
                    }
                }
            }
        }
    }

    return function () {
        resolveObject(arguments[0]);
    };
}();