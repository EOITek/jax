import marked from 'marked';
import hljs from 'highlight.js';
import 'highlight.js/styles/default.css';
import 'github-markdown-css';

import { Component, Prop, VueComponentBase } from '@/common/VueComponentBase';

@Component()
export default class MarkdownHtml extends VueComponentBase {
  @Prop() readonly data: string;
  @Prop() readonly option: Record<string, any>;
  @Prop() height = '500px';

  markdownData: string = null;

  created() {
    this.generateMarkdownHtml();
    this.markdownData = marked(this.data);
  }

  generateMarkdownHtml() {
    const defaultOption = {
      renderer: new marked.Renderer(),
      highlight(data){
        return hljs.highlightAuto(data).value;
      },
      pedantic: false,
      gfm: true,
      tables: true,
      breaks: false,
      sanitize: false,
      smartLists: true,
      smartypants: false,
      xhtml: false,
    };
    marked.setOptions(Object.assign(defaultOption, this.option));
  }
}
